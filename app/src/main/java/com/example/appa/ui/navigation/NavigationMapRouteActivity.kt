package com.example.appa.ui.navigation

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.appa.R
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapLongClickListener
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.camera.NavigationCamera
import com.mapbox.navigation.ui.route.NavigationMapRoute
import kotlinx.android.synthetic.main.activity_navigation_map_route.*
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*


/**
 * This activity demonstrates turn by turn navigation using the NavigationMapRoute class. This can
 * be used instead of the convenience class NavigationMapboxMap if it suits your needs.
 * For reference, view https://github.com/mapbox/mapbox-navigation-android/blob/master/examples/src/main/java/com/mapbox/navigation/examples/core/NavigationMapRouteActivity.java
 */
class NavigationMapRouteActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {


    private var mapboxMap: MapboxMap? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var mapboxNavigation: MapboxNavigation? = null
    private var mapCamera: NavigationCamera? = null
    private var activeRoute: DirectionsRoute? = null
    private val mapboxReplayer = MapboxReplayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_navigation_map_route)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val startbutton = findViewById<Button>(R.id.startNavigationButton)
        startbutton.setOnClickListener {
            onStartNavigation()
        }
    }

    //start navigation on button click after selecting destination
    @SuppressLint("MissingPermission")
    fun onStartNavigation() {
        mapboxMap!!.locationComponent.cameraMode = CameraMode.TRACKING_GPS
        mapboxMap!!.locationComponent.renderMode = RenderMode.GPS
        mapCamera!!.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
        mapCamera!!.start(activeRoute)
        mapboxNavigation!!.startTripSession()
        startNavigationButton!!.visibility = View.GONE
    }

    //this is where you register observers for MapboxNavigation object
    @SuppressLint("MissingPermission")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style: Style ->
            initializeLocationComponent(mapboxMap, style)
            val navigationOptions = MapboxNavigation.defaultNavigationOptionsBuilder(this, getString(R.string.mapbox_access_token))
                    .locationEngine(ReplayLocationEngine(mapboxReplayer))
                    .build()
            mapboxNavigation = MapboxNavigation(navigationOptions)
            mapboxNavigation!!.registerLocationObserver(locationObserver)
            mapboxNavigation!!.registerRouteProgressObserver(replayProgressObserver)
            mapboxReplayer.pushRealLocation(this, 0.0)
            mapboxReplayer.play()
            mapCamera = NavigationCamera(mapboxMap)
            mapCamera!!.addProgressChangeListener(mapboxNavigation!!)
            navigationMapRoute = NavigationMapRoute.Builder(mapView!!, mapboxMap, this)
                    .withVanishRouteLineEnabled(true)
                    .withMapboxNavigation(mapboxNavigation)
                    .build()
            mapboxNavigation!!.navigationOptions.locationEngine.getLastLocation(locationEngineCallback)
            mapboxMap.addOnMapLongClickListener(this)
            if (activeRoute != null) {
                val routes: List<DirectionsRoute> = Arrays.asList(activeRoute) as List<DirectionsRoute>
                navigationMapRoute!!.addRoutes(routes)
                mapboxNavigation!!.setRoutes(routes)
                startNavigationButton!!.visibility = View.VISIBLE
            } else {
                Snackbar.make(mapView!!, R.string.msg_long_press_map_to_place_waypoint, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapLongClick(point: LatLng): Boolean {
        handleClicked(point)
        return true
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
        if (mapboxNavigation != null) {
            mapboxNavigation!!.registerLocationObserver(locationObserver)
        }
        if (mapCamera != null) {
            mapCamera!!.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        mapCamera!!.onStop()
        mapboxNavigation!!.unregisterLocationObserver(locationObserver)
        mapView!!.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapboxNavigation!!.unregisterRouteProgressObserver(replayProgressObserver)
        mapboxNavigation!!.stopTripSession()
        mapboxNavigation!!.onDestroy()
        mapView!!.onDestroy()
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)

        // This is not the most efficient way to preserve the route on a device rotation.
        // This is here to demonstrate that this event needs to be handled in order to
        // redraw the route line after a rotation.
        if (activeRoute != null) {
            outState.putString(PRIMARY_ROUTE_BUNDLE_KEY, activeRoute!!.toJson())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        activeRoute = getRouteFromBundle(savedInstanceState)
    }*/

    @SuppressLint("MissingPermission")
    private fun initializeLocationComponent(mapboxMap: MapboxMap, style: Style) {
        val activationOptions = LocationComponentActivationOptions.builder(this, style)
                .useDefaultLocationEngine(false)
                .build()
        val locationComponent = mapboxMap.locationComponent
        locationComponent.activateLocationComponent(activationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.renderMode = RenderMode.COMPASS
        locationComponent.cameraMode = CameraMode.TRACKING
    }

    //executes when long click on map. sets up route
    private fun handleClicked(touchLocation: LatLng) {
        //vibrate()
        hideRoute()
        val currentLocation = mapboxMap!!.locationComponent.lastKnownLocation
        if (currentLocation != null) {
            val originPoint = Point.fromLngLat(
                    currentLocation.longitude,
                    currentLocation.latitude
            )
            val destinationPoint = Point.fromLngLat(touchLocation.longitude, touchLocation.latitude)
            findRoute(originPoint, destinationPoint)
            routeLoadingProgressBar!!.visibility = View.VISIBLE
        }
    }

    /*@SuppressLint("MissingPermission")
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ONE_HUNDRED_MILLISECONDS.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(ONE_HUNDRED_MILLISECONDS.toLong())
        }
    }*/

    //hides route on map after starting navigation
    private fun hideRoute() {
        navigationMapRoute!!.updateRouteVisibilityTo(false)
        startNavigationButton!!.visibility = View.GONE
    }

    //builds route after passing origin and destination Point objects
    fun findRoute(origin: Point?, destination: Point?) {
        val routeOptions = RouteOptions.builder()
                .baseUrl(RouteUrl.BASE_URL)
                .user(RouteUrl.PROFILE_DEFAULT_USER)
                .profile(RouteUrl.PROFILE_WALKING)
                .geometries(RouteUrl.GEOMETRY_POLYLINE6)
                .requestUuid("")
                .accessToken(getString(R.string.mapbox_access_token))
                .coordinates(Arrays.asList(origin, destination))
                .alternatives(false)
                .build()
        mapboxNavigation!!.requestRoutes(
                routeOptions,
                routesReqCallback
        )
    }

    private val routesReqCallback: RoutesRequestCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (!routes.isEmpty()) {
                activeRoute = routes[0]
                navigationMapRoute!!.addRoutes(routes)
                routeLoadingProgressBar!!.visibility = View.INVISIBLE
                startNavigationButton!!.visibility = View.VISIBLE
            }
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            Timber.e("route request failure %s", throwable.toString())
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            Timber.d("route request canceled")
        }
    }
    private val locationEngineCallback = MyLocationEngineCallback(this)

    private class MyLocationEngineCallback internal constructor(activity: NavigationMapRouteActivity) : LocationEngineCallback<LocationEngineResult> {
        private val activityRef: WeakReference<NavigationMapRouteActivity>
        override fun onSuccess(result: LocationEngineResult) {
            activityRef.get()!!.updateLocation(result.locations)
        }

        override fun onFailure(exception: Exception) {
            Timber.i(exception)
        }

        init {
            activityRef = WeakReference(activity)
        }
    }

    private fun updateLocation(location: Location) {
        updateLocation(Arrays.asList(location))
    }

    private fun updateLocation(locations: List<Location>) {
        mapboxMap!!.locationComponent.forceLocationUpdate(locations, false)
    }

    private val locationObserver: LocationObserver = object : LocationObserver {
        override fun onRawLocationChanged(rawLocation: Location) {
            Timber.d("raw location %s", rawLocation.toString())
        }

        override fun onEnhancedLocationChanged(
                enhancedLocation: Location,
                keyPoints: List<Location>
        ) {
            if (keyPoints.isEmpty()) {
                updateLocation(enhancedLocation)
            } else {
                updateLocation(keyPoints)
            }
        }
    }
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)

    companion object {
        private const val ONE_HUNDRED_MILLISECONDS = 100
    }
}