@file:Suppress("DEPRECATION")

package com.example.appa.ui.navigation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.directions.v5.models.VoiceInstructions
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.base.internal.extensions.applyDefaultParams
import com.mapbox.navigation.base.internal.extensions.coordinates
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.telemetry.events.FeedbackEvent.UI
import com.mapbox.navigation.core.trip.session.BannerInstructionsObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.TripSessionState
import com.mapbox.navigation.core.trip.session.TripSessionStateObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.example.appa.R
import com.example.appa.db.PlaceEntity
import com.example.appa.viewmodel.MapWithNavViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.ui.NavigationButton
import com.mapbox.navigation.ui.NavigationConstants
import com.mapbox.navigation.ui.SoundButton
import com.mapbox.navigation.ui.camera.NavigationCamera
import com.mapbox.navigation.ui.feedback.FeedbackBottomSheet
import com.mapbox.navigation.ui.feedback.FeedbackBottomSheetListener
import com.mapbox.navigation.ui.feedback.FeedbackItem
import com.mapbox.navigation.ui.internal.utils.BitmapEncodeOptions
import com.mapbox.navigation.ui.internal.utils.ViewUtils
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import com.mapbox.navigation.ui.summary.SummaryBottomSheet
import com.mapbox.navigation.ui.voice.NavigationSpeechPlayer
import com.mapbox.navigation.ui.voice.SpeechPlayerProvider
import com.mapbox.navigation.ui.voice.VoiceInstructionLoader
import kotlinx.android.synthetic.main.activity_instruction_view_layout.*
import kotlinx.android.synthetic.main.activity_instruction_view_layout.mapView
import kotlinx.android.synthetic.main.activity_instruction_view_layout.recenterBtn
import kotlinx.android.synthetic.main.activity_instruction_view_layout.startNavigation
import kotlinx.android.synthetic.main.activity_instruction_view_layout.summaryBottomSheet
import kotlinx.android.synthetic.main.activity_summary_bottom_sheet.*
import okhttp3.Cache
import java.io.File
import java.lang.ref.WeakReference
import java.util.Locale

/**
 * This activity shows how to integrate the Navigation UI SDK's
 * InstructionView, FeedbackButton, and SoundButton with
 * the Navigation SDK.
 */
class InstructionViewActivity :
        AppCompatActivity(),
        OnMapReadyCallback,
        FeedbackBottomSheetListener {

    private lateinit var viewModel: MapWithNavViewModel
    private var currentPlace: PlaceEntity? = null
    private var currentPlaceID: Int? = null

    private var mapboxNavigation: MapboxNavigation? = null
    private var navigationMapboxMap: NavigationMapboxMap? = null
    private lateinit var speechPlayer: NavigationSpeechPlayer
    private lateinit var destination: LatLng
    private val mapboxReplayer = MapboxReplayer()

    private var mapboxMap: MapboxMap? = null
    private var feedbackButton: NavigationButton? = null
    private var instructionSoundButton: NavigationButton? = null
    private var directionRoute: DirectionsRoute? = null

    private var feedbackItem: FeedbackItem? = null
    private var feedbackEncodedScreenShot: String? = null

    private lateinit var summaryBehavior: BottomSheetBehavior<SummaryBottomSheet>
    private lateinit var routeOverviewButton: ImageButton
    private lateinit var cancelBtn: AppCompatImageButton
    private val routeOverviewPadding by lazy { buildRouteOverviewPadding() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_instruction_view_layout)
        initViews()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        viewModel = ViewModelProvider(this)[MapWithNavViewModel::class.java]
        setPlaceFromIntent()

        val mapboxNavigationOptions = MapboxNavigation
                .defaultNavigationOptionsBuilder(this, getString(R.string.mapbox_access_token))
                .locationEngine(getLocationEngine())
                .build()

        mapboxNavigation = MapboxNavigation(mapboxNavigationOptions).apply {
            registerTripSessionStateObserver(tripSessionStateObserver)
            registerRouteProgressObserver(routeProgressObserver)
            registerBannerInstructionsObserver(bannerInstructionObserver)
            registerVoiceInstructionsObserver(voiceInstructionsObserver)
        }

        initListeners()
        initializeSpeechPlayer()
    }

    private fun setPlaceFromIntent() {
        // Get the intent, apply it to the current place ID,
        val intent = intent
        currentPlaceID = intent.getIntExtra("NewPlace", 1)
        val placeEntityObserver = Observer<PlaceEntity> { placeEntity ->
            currentPlace = placeEntity
        }
        viewModel.getPlaceFromID(currentPlaceID).observeForever(placeEntityObserver)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
        setPlaceFromIntent()

    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapboxReplayer.finish()
        mapboxNavigation?.apply {
            unregisterTripSessionStateObserver(tripSessionStateObserver)
            unregisterRouteProgressObserver(routeProgressObserver)
            unregisterBannerInstructionsObserver(bannerInstructionObserver)
            unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
            stopTripSession()
            onDestroy()
        }

        speechPlayer.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
        // This is not the most efficient way to preserve the route on a device rotation.
        // This is here to demonstrate that this event needs to be handled in order to
        // redraw the route line after a rotation.
        directionRoute?.let {
            outState.putString("RouteBundleKey", it.toJson())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // "If you save the state of the application in a bundle (typically non-persistent,
        // dynamic data in onSaveInstanceState), it can be passed back to onCreate
        // if the activity needs to be recreated (e.g., orientation change)
        // so that you don't lose this prior information.
        // If no data was supplied, savedInstanceState is null."

        super.onRestoreInstanceState(savedInstanceState)

        try {
            if (savedInstanceState.containsKey("RouteBundleKey")) {
                val routeAsJson: String? = savedInstanceState.getString("RouteBundleKey")
                directionRoute = DirectionsRoute.fromJson(routeAsJson)
            }
        } catch (ex: Exception) {
            Log.e("Error", ex.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationComponent(mapboxMap: MapboxMap, style: Style) {
        val activationOptions = LocationComponentActivationOptions.builder(this, style)
                .useDefaultLocationEngine(false)
                .build()
        mapboxMap.locationComponent.activateLocationComponent(activationOptions)
        mapboxMap.locationComponent.isLocationComponentEnabled = true
        mapboxMap.locationComponent.renderMode = RenderMode.COMPASS
        mapboxMap.locationComponent.cameraMode = CameraMode.TRACKING
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            initializeLocationComponent(mapboxMap, it)
            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(15.0))
            navigationMapboxMap = NavigationMapboxMap(mapView, mapboxMap, this, true)

            when (directionRoute) {
                null -> {
                    if (shouldSimulateRoute()) {
                        mapboxNavigation?.registerRouteProgressObserver(ReplayProgressObserver(mapboxReplayer))
                        mapboxReplayer.pushRealLocation(this, 0.0)
                        mapboxReplayer.play()
                    }
                    mapboxNavigation
                            ?.navigationOptions
                            ?.locationEngine
                            ?.getLastLocation(locationListenerCallback)
                    // Snackbar.make(container, R.string.msg_long_press_map_to_place_waypoint, LENGTH_SHORT).show()

                    if (currentPlace != null) {
                        val destinationLong = currentPlace!!.longitude.toDouble()
                        val destinationLat = currentPlace!!.latitude.toDouble()
                        val destinationPoint = Point.fromLngLat(destinationLong, destinationLat)
                        val originLong = mapboxMap.locationComponent.lastKnownLocation!!.longitude
                        val originLat = mapboxMap.locationComponent.lastKnownLocation!!.latitude
                        val originPoint = Point.fromLngLat(originLong, originLat);

                        mapboxNavigation?.requestRoutes(
                                RouteOptions.builder()
                                        .applyDefaultParams()
                                        .profile(RouteUrl.PROFILE_WALKING)
                                        .accessToken(getString(R.string.mapbox_access_token))
                                        .coordinates(listOf(originPoint, destinationPoint))
                                        .build(), routesReqCallback
                        )
                    }
                }
                else -> restoreNavigation()
            }
        }
    }

    // InstructionView Feedback Bottom Sheet listener
    override fun onFeedbackDismissed() {
        // do nothing
    }

    override fun onFeedbackSelected(feedbackItem: FeedbackItem?) {
        feedbackItem?.let { feedback ->
            this.feedbackItem = feedback
            sendFeedback()
        }
    }

    private fun encodeSnapshot(snapshot: Bitmap) {
        screenshotView.visibility = VISIBLE
        screenshotView.setImageBitmap(snapshot)
        mapView.visibility = View.INVISIBLE
        feedbackEncodedScreenShot = ViewUtils.encodeView(
                ViewUtils.captureView(mapView),
                BitmapEncodeOptions.Builder()
                        .width(400).compressQuality(40).build()
        )
        screenshotView.visibility = View.INVISIBLE
        mapView.visibility = VISIBLE

        sendFeedback()
    }

    private fun sendFeedback() {
        val feedback = feedbackItem
        val screenShot = feedbackEncodedScreenShot
        if (feedback != null && !screenShot.isNullOrEmpty()) {
            mapboxNavigation?.postUserFeedback(
                    feedback.feedbackType,
                    feedback.description,
                    UI,
                    screenShot,
                    feedback.feedbackSubType.toTypedArray()
            )

            // Daniel: Not sure where this feedback function is defined.
            //showFeedbackSentSnackBar(context = this, view = mapView)
        }
    }


    // Calling this function starts the navigation
    // On the currently selected location.
    @SuppressLint("MissingPermission")
    private fun beginNavigation() {
        updateCameraOnNavigationStateChange(true)
        navigationMapboxMap?.addOnCameraTrackingChangedListener(cameraTrackingChangedListener)
        navigationMapboxMap?.addProgressChangeListener(mapboxNavigation!!)
        if (mapboxNavigation?.getRoutes()?.isNotEmpty() == true) {
            navigationMapboxMap?.startCamera(mapboxNavigation?.getRoutes()!![0])
        }
        mapboxNavigation?.startTripSession()
    }

    @SuppressLint("MissingPermission")
    private fun initListeners() {
        startNavigation.setOnClickListener {
            updateCameraOnNavigationStateChange(true)
            navigationMapboxMap?.addOnCameraTrackingChangedListener(cameraTrackingChangedListener)
            navigationMapboxMap?.addProgressChangeListener(mapboxNavigation!!)
            if (mapboxNavigation?.getRoutes()?.isNotEmpty() == true) {
                navigationMapboxMap?.startCamera(mapboxNavigation?.getRoutes()!![0])
            }
            mapboxNavigation?.startTripSession()
        }

        summaryBehavior.setBottomSheetCallback(bottomSheetCallback)

        routeOverviewButton.setOnClickListener {
            navigationMapboxMap?.showRouteOverview(routeOverviewPadding)
            recenterBtn.show()
        }

        recenterBtn.addOnClickListener {
            recenterBtn.hide()
            navigationMapboxMap?.resetPadding()
            navigationMapboxMap
                    ?.resetCameraPositionWith(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
        }

        cancelBtn.setOnClickListener {
            mapboxNavigation?.stopTripSession()
            updateCameraOnNavigationStateChange(false)
        }
    }

    private fun buildRouteOverviewPadding(): IntArray {
        val leftRightPadding =
                resources
                        .getDimension(
                                com.mapbox.navigation.ui.R.dimen.mapbox_route_overview_left_right_padding
                        )
                        .toInt()
        val paddingBuffer =
                resources
                        .getDimension(
                                com.mapbox.navigation.ui.R.dimen.mapbox_route_overview_buffer_padding
                        )
                        .toInt()
        val instructionHeight =
                (
                        resources
                                .getDimension(
                                        com.mapbox.navigation.ui.R.dimen.mapbox_instruction_content_height
                                ) +
                                paddingBuffer
                        )
                        .toInt()
        val summaryHeight =
                resources
                        .getDimension(com.mapbox.navigation.ui.R.dimen.mapbox_summary_bottom_sheet_height)
                        .toInt()
        return intArrayOf(leftRightPadding, instructionHeight, leftRightPadding, summaryHeight)
    }

    private fun isLocationTracking(cameraMode: Int): Boolean {
        return cameraMode == CameraMode.TRACKING ||
                cameraMode == CameraMode.TRACKING_COMPASS ||
                cameraMode == CameraMode.TRACKING_GPS ||
                cameraMode == CameraMode.TRACKING_GPS_NORTH
    }

    private fun initializeSpeechPlayer() {
        val cache =
                Cache(File(application.cacheDir, VOICE_INSTRUCTION_CACHE), 10 * 1024 * 1024)
        val voiceInstructionLoader =
                VoiceInstructionLoader(application, Mapbox.getAccessToken(), cache)
        val speechPlayerProvider =
                SpeechPlayerProvider(application, Locale.US.language, true, voiceInstructionLoader)
        speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!shouldSimulateRoute()) {
            val requestLocationUpdateRequest =
                    LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                            .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                            .setMaxWaitTime(1000)
                            .build()

            mapboxNavigation?.navigationOptions?.locationEngine?.requestLocationUpdates(
                    requestLocationUpdateRequest,
                    locationListenerCallback,
                    mainLooper
            )
        }
    }

    private fun stopLocationUpdates() {
        if (!shouldSimulateRoute()) {
            mapboxNavigation?.navigationOptions?.locationEngine?.removeLocationUpdates(
                    locationListenerCallback
            )
        }
    }

    private fun initViews() {
        startNavigation.visibility = VISIBLE
        startNavigation.isEnabled = false

        summaryBottomSheet.visibility = GONE
        summaryBehavior = BottomSheetBehavior.from(summaryBottomSheet).apply {
            isHideable = false
        }
        recenterBtn.hide()
        routeOverviewButton = findViewById(R.id.routeOverviewBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        instructionView.visibility = GONE
        feedbackButton = instructionView.retrieveFeedbackButton().apply {
            hide()
            addOnClickListener {
                feedbackItem = null
                feedbackEncodedScreenShot = null
                supportFragmentManager.let {
                    mapboxMap?.snapshot(this@InstructionViewActivity::encodeSnapshot)
                    FeedbackBottomSheet.newInstance(
                            this@InstructionViewActivity,
                            NavigationConstants.FEEDBACK_BOTTOM_SHEET_DURATION
                    )
                            .show(it, FeedbackBottomSheet.TAG)
                }
            }
        }
        instructionSoundButton = instructionView.retrieveSoundButton().apply {
            hide()
            addOnClickListener {
                val soundButton = instructionSoundButton
                if (soundButton is SoundButton) {
                    speechPlayer.isMuted = soundButton.toggleMute()
                }
            }
        }
    }

    private fun updateViews(tripSessionState: TripSessionState) {
        when (tripSessionState) {
            TripSessionState.STARTED -> {
                startNavigation.visibility = GONE
                summaryBottomSheet.visibility = VISIBLE
                recenterBtn.hide()
                instructionView.visibility = VISIBLE
                feedbackButton?.show()
                instructionSoundButton?.show()
            }
            TripSessionState.STOPPED -> {
                startNavigation.visibility = VISIBLE
                startNavigation.isEnabled = false
                summaryBottomSheet.visibility = GONE
                recenterBtn.hide()
                instructionView.visibility = GONE
                feedbackButton?.hide()
                instructionSoundButton?.hide()
            }
        }
    }

    private fun updateCameraOnNavigationStateChange(
            navigationStarted: Boolean
    ) {
        navigationMapboxMap?.apply {
            if (navigationStarted) {
                updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
                updateLocationLayerRenderMode(RenderMode.GPS)
            } else {
                updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE)
                updateLocationLayerRenderMode(RenderMode.COMPASS)
            }
        }
    }

    private val routesReqCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (routes.isNotEmpty()) {
                directionRoute = routes[0]
                navigationMapboxMap?.drawRoute(routes[0])
                startNavigation.visibility = VISIBLE
                startNavigation.isEnabled = true
            } else {
                startNavigation.isEnabled = false
            }
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
        }
    }

    private val locationListenerCallback = MyLocationEngineCallback(this)

    private val tripSessionStateObserver = object : TripSessionStateObserver {
        override fun onSessionStateChanged(tripSessionState: TripSessionState) {
            when (tripSessionState) {
                TripSessionState.STARTED -> {
                    updateViews(TripSessionState.STARTED)
                    stopLocationUpdates()
                }
                TripSessionState.STOPPED -> {
                    updateViews(TripSessionState.STOPPED)
                    startLocationUpdates()
                    navigationMapboxMap?.hideRoute()
                    updateCameraOnNavigationStateChange(false)
                }
            }
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (summaryBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                recenterBtn.show()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private val cameraTrackingChangedListener = object : OnCameraTrackingChangedListener {
        override fun onCameraTrackingChanged(currentMode: Int) {
            if (isLocationTracking(currentMode)) {
                summaryBehavior.isHideable = false
                summaryBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onCameraTrackingDismissed() {
            if (mapboxNavigation?.getTripSessionState() == TripSessionState.STARTED) {
                summaryBehavior.isHideable = true
                summaryBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


    /* These should be the methods that allow us to retrieve instructions and insert them into an activity */
    private val routeProgressObserver = object : RouteProgressObserver {
        override fun onRouteProgressChanged(routeProgress: RouteProgress) {
            instructionView.updateDistanceWith(routeProgress)
            summaryBottomSheet.update(routeProgress)
        }
    }

    private val bannerInstructionObserver = object : BannerInstructionsObserver {
        override fun onNewBannerInstructions(bannerInstructions: BannerInstructions) {
            instructionView.updateBannerInstructionsWith(bannerInstructions)
        }
    }

    private val voiceInstructionsObserver = object : VoiceInstructionsObserver {
        override fun onNewVoiceInstructions(voiceInstructions: VoiceInstructions) {
            speechPlayer.play(voiceInstructions)
        }
    }

    // Used to determine if the ReplayRouteLocationEngine should be used to simulate the routing.
    // This is used for testing purposes.
    private fun shouldSimulateRoute(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
                .getBoolean(this.getString(R.string.simulate_route_key), false)
    }

    // If shouldSimulateRoute is true a ReplayRouteLocationEngine will be used which is intended
    // for testing else a real location engine is used.
    private fun getLocationEngine(): LocationEngine {
        return if (shouldSimulateRoute()) {
            ReplayLocationEngine(mapboxReplayer)
        } else {
            LocationEngineProvider.getBestLocationEngine(this)
        }
    }

    private class MyLocationEngineCallback(activity: InstructionViewActivity) :
            LocationEngineCallback<LocationEngineResult> {

        private val activityRef = WeakReference(activity)

        override fun onSuccess(result: LocationEngineResult) {
            activityRef.get()?.navigationMapboxMap?.updateLocation(result.lastLocation)
        }

        override fun onFailure(exception: Exception) {
        }
    }

    companion object {
        const val VOICE_INSTRUCTION_CACHE = "voice-instruction-cache"
        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    }

    @SuppressLint("MissingPermission")
    private fun restoreNavigation() {
        directionRoute?.let {
            mapboxNavigation?.setRoutes(listOf(it))
            navigationMapboxMap?.addProgressChangeListener(mapboxNavigation!!)
            navigationMapboxMap?.startCamera(mapboxNavigation?.getRoutes()!![0])
            updateCameraOnNavigationStateChange(true)
            mapboxNavigation?.startTripSession()
        }
    }
}
