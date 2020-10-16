package com.example.appa.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.appa.R
import com.example.appa.db.PlaceEntity
import com.example.appa.viewmodel.MapWithNavViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
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
        OnMapReadyCallback {
        private var viewModel: MapWithNavViewModel? = null
        private var currentPlace: PlaceEntity? = null
        private var currentPlaceID: Int? = null
        private var mapboxNavigation: MapboxNavigation? = null
        private var navigationMapRoute: NavigationMapRoute? = null
        private var originPoint: Point? = null
        private var destinationPoint: Point? = null
        private lateinit var MAPBOXTOKEN: String
        private var mapboxMap: MapboxMap? = null
        private lateinit var routeProgressObserver: RouteProgressObserver
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            MAPBOXTOKEN = getString(R.string.mapbox_access_token)
            Mapbox.getInstance(this, MAPBOXTOKEN)

            setContentView(R.layout.activity_directions)

            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this)

            val mapboxNavigationOptions = MapboxNavigation
                    .defaultNavigationOptionsBuilder(this, MAPBOXTOKEN)
                    .build()
            mapboxNavigation = MapboxNavigation(mapboxNavigationOptions)
            viewModel = ViewModelProvider(this)[MapWithNavViewModel::class.java]

            setPlaceFromIntent()
            routeProgressObserver = object:  RouteProgressObserver {
                override fun onRouteProgressChanged(routeProgress: RouteProgress) {
                    println(routeProgress)
                }
            }
        }



    private fun enableLocationComponent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mapboxMap?.getStyle {
            mapboxMap?.locationComponent?.apply {
                activateLocationComponent(
                        LocationComponentActivationOptions.builder(
                                this@CustomDirectionsActivity,
                                it
                        )
                                .build()
                )

                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }
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
            fun onChanged(placeEntity: PlaceEntity) {
                currentPlace = placeEntity;
                val destinationLong = currentPlace!!.longitude.toDouble()
                val destinationLat = currentPlace!!.latitude.toDouble()
                destinationPoint = Point.fromLngLat(destinationLong, destinationLat)

                val originLong = mapboxMap!!.locationComponent.lastKnownLocation!!.longitude
                val originLat = mapboxMap!!.locationComponent.lastKnownLocation!!.latitude
                originPoint = Point.fromLngLat(originLong, originLat);

                mapboxNavigation?.requestRoutes(
                        RouteOptions.builder()
                                .applyDefaultParams()
                                .accessToken(MAPBOXTOKEN)
                                .coordinates(listOf(originPoint, destinationPoint))
                                .build(), routesReqCallback
                )
            }
        }

        viewModel?.getPlaceFromID(currentPlaceID)?.observe(this, placeEntityObserver)

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.LIGHT) {
            this.mapboxMap = mapboxMap
            enableLocationComponent()
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
                navigationMapRoute?.addRoute(routes[0])
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

}