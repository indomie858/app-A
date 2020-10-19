package com.example.appa.ui.navigation

import android.graphics.Color.parseColor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.appa.R
import com.example.appa.db.PlaceEntity
import com.example.appa.viewmodel.MapWithNavViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.mapbox.navigation.base.internal.extensions.applyDefaultParams
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.route.NavigationMapRoute
import kotlinx.android.synthetic.main.activity_directions.*


class CustomDirectionsActivity:
        AppCompatActivity(),
        PermissionsListener,
        OnMapReadyCallback {
        private lateinit var viewModel: MapWithNavViewModel
        private var currentPlace: PlaceEntity? = null
        private var currentPlaceID: Int? = null
        private var mapboxNavigation: MapboxNavigation? = null
        private var navigationMapRoute: NavigationMapRoute? = null
        private var originPoint: Point? = null
        private var destinationPoint: Point? = null
        private lateinit var MAPBOXTOKEN: String
        private lateinit var mapboxMap: MapboxMap
        private lateinit var routeProgressObserver: RouteProgressObserver
        private var activeRoute: DirectionsRoute? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            MAPBOXTOKEN = getString(R.string.mapbox_access_token)
            Mapbox.getInstance(this, MAPBOXTOKEN)

            setContentView(R.layout.activity_directions)

            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this)

            viewModel = ViewModelProvider(this)[MapWithNavViewModel::class.java]
            setPlaceFromIntent()
            val mapboxNavigationOptions = MapboxNavigation
                    .defaultNavigationOptionsBuilder(this, MAPBOXTOKEN)
                    .build()
            mapboxNavigation = MapboxNavigation(mapboxNavigationOptions)
            println("CREATION")
            routeProgressObserver = object:  RouteProgressObserver {
                override fun onRouteProgressChanged(routeProgress: RouteProgress) {
                    println(routeProgress)
                }
            }

        }


    private fun initializeLocationComponent(mapboxMap: MapboxMap, style: Style) {
        if(PermissionsManager.areLocationPermissionsGranted(this) == true) {
            val activationOptions = LocationComponentActivationOptions.builder(this, style)
                    .useDefaultLocationEngine(false)
                    .build()
            val locationComponent: LocationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(activationOptions)
            locationComponent.setLocationComponentEnabled(true)
            locationComponent.setRenderMode(RenderMode.COMPASS)
            locationComponent.setCameraMode(CameraMode.TRACKING)
        }
        else {
            val permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }
    override fun onResume() {
        super.onResume()
        setPlaceFromIntent()

    }

    private fun setPlaceFromIntent() {
        // Get the intent, apply it to the current place ID,
        val intent = intent
        currentPlaceID = intent.getIntExtra("NewPlace", 1)
        val placeEntityObserver = Observer<PlaceEntity> {
            placeEntity -> currentPlace = placeEntity
        }
        viewModel.getPlaceFromID(currentPlaceID).observeForever(placeEntityObserver)
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.LIGHT) {
            initializeLocationComponent(this.mapboxMap, it)
            if(currentPlace != null) {
                val destinationLong = currentPlace!!.longitude.toDouble()
                val destinationLat = currentPlace!!.latitude.toDouble()
                destinationPoint = Point.fromLngLat(destinationLong, destinationLat)
                val originLong = this.mapboxMap.locationComponent.lastKnownLocation!!.longitude
                val originLat = this.mapboxMap.locationComponent.lastKnownLocation!!.latitude
                originPoint = Point.fromLngLat(originLong, originLat);

                mapboxNavigation?.requestRoutes(
                        RouteOptions.builder()
                                .applyDefaultParams()
                                .accessToken(MAPBOXTOKEN)
                                .coordinates(listOf(originPoint, destinationPoint))
                                .build(), routesReqCallback
                )
            }


            // Add the click and route sources
            it.addSource(GeoJsonSource("CLICK_SOURCE"))
            it.addSource(
                    GeoJsonSource(
                            "ROUTE_LINE_SOURCE_ID",
                            GeoJsonOptions().withLineMetrics(true)
                    )
            )
        // Add the destination marker image
            it.addImage(
                    "ICON_ID",
                    BitmapUtils.getBitmapFromDrawable(
                            ContextCompat.getDrawable(
                                    this,
                                    R.drawable.mapbox_marker_icon_default
                            )
                    )!!
            )
        // Add the LineLayer below the LocationComponent's bottom layer, which is the
        // circular accuracy layer. The LineLayer will display the directions route.
            it.addLayerBelow(
                    LineLayer("ROUTE_LAYER_ID", "ROUTE_LINE_SOURCE_ID")
                            .withProperties(
                                    lineCap(LINE_CAP_ROUND),
                                    lineJoin(LINE_JOIN_ROUND),
                                    lineWidth(6f),
                                    lineGradient(
                                            interpolate(
                                                    linear(),
                                                    lineProgress(),
                                                    stop(0f, color(parseColor("#32a852"))),
                                                    stop(1f, color(parseColor("#F84D4D")))
                                            )
                                    )
                            ),
                    "mapbox-location-shadow-layer"
            )
            // Add the SymbolLayer to show the destination marker
            it.addLayerAbove(
                    SymbolLayer("CLICK_LAYER", "CLICK_SOURCE")
                            .withProperties(
                                    iconImage("ICON_ID")
                            ),
                    "ROUTE_LAYER_ID"
            )


            navigationMapRoute = NavigationMapRoute.Builder(mapView, mapboxMap, this)
                    .withVanishRouteLineEnabled(true)
                    .withMapboxNavigation(mapboxNavigation)
                    .build()

            if (activeRoute != null) {
                val aRoute: DirectionsRoute = activeRoute as DirectionsRoute
                val routes: List<DirectionsRoute> = listOf(aRoute)
                navigationMapRoute!!.addRoutes(routes)
                mapboxNavigation!!.setRoutes(routes)
            }
        }
    }

    private val routesReqCallback = object : RoutesRequestCallback {

        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (routes.isNotEmpty()) {
                Snackbar.make(
                        mapView,
                        String.format(
                                routes[0].legs()?.get(0)?.steps()?.size.toString()
                        ),
                        LENGTH_SHORT
                ).show()
                activeRoute = routes[0]
                mapboxMap?.getStyle {
                    val clickPointSource = it.getSourceAs<GeoJsonSource>("ROUTE_LINE_SOURCE_ID")
                    val routeLineString = LineString.fromPolyline(
                            routes[0].geometry()!!,
                            6
                    )
                    clickPointSource?.setGeoJson(routeLineString)
                }
            }
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            println("Routes request failure")
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            println("Route request cancelled")
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted) {
            mapboxMap.style?.let { initializeLocationComponent(mapboxMap, it) }
        }
        TODO("Not yet implemented")
    }

}