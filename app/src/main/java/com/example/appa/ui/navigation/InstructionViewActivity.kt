package com.example.appa.ui.navigation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.appa.R
import com.example.appa.db.PlaceEntity
import com.example.appa.ui.BeaconReferenceApplication
import com.example.appa.viewmodel.MapWithNavViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.directions.v5.models.VoiceInstructions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.base.internal.extensions.applyDefaultParams
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.base.trip.model.RouteProgressState
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.telemetry.events.FeedbackEvent.UI
import com.mapbox.navigation.core.trip.session.*
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
import okhttp3.Cache
import org.altbeacon.beacon.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * This activity shows how to integrate the Navigation UI SDK's
 * InstructionView, FeedbackButton, and SoundButton with
 * the Navigation SDK.
 */
class InstructionViewActivity :
        AppCompatActivity(),
        OnMapReadyCallback,
        FeedbackBottomSheetListener, BeaconConsumer {
    // SO MANY MEMBERS
    private lateinit var viewModel: MapWithNavViewModel
    private var currentPlace: PlaceEntity? = null
    private var currentPlaceID: Int? = null

    private var mapboxNavigation: MapboxNavigation? = null
    private var locationComponent: LocationComponent? = null
    private var navigationMapboxMap: NavigationMapboxMap? = null
    private lateinit var speechPlayer: NavigationSpeechPlayer
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

    private val TAG = "InstructionViewActivity"
    private val beaconManager = BeaconManager.getInstanceForApplication(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_instruction_view_layout)

        // This handles the back navigation button on top app bar
        val actionbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        if (null != actionbar) {
            actionbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            actionbar.setNavigationOnClickListener { NavUtils.navigateUpFromSameTask(this@InstructionViewActivity) }
        }

        verifyBluetooth()

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
    }//end of onCreate function

    //////////////////////////////Beacon functions begin//////////////////////////////////////////
    private fun verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                val builder = AlertDialog.Builder(this)

                builder.setTitle("Bluetooth not enabled")
                builder.setMessage("Please enable bluetooth in settings and restart this application.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    //finish();
                    //System.exit(0);
                }
                builder.show()
            }
        } catch (e: RuntimeException) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bluetooth LE not available")
            builder.setMessage("Sorry, this device does not support Bluetooth LE.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                //finish();
                //System.exit(0);
            }
            builder.show()
        }
    }

    //Called when the beacon service is running and ready to accept your commands through the BeaconManager
    override fun onBeaconServiceConnect() {
        //ToneGenerator class contains various system sounds...beeps boops and whatnot
        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        //Called once per second to give an estimate of the mDistance to visible beacons
        val rangeNotifier = RangeNotifier { beacons, region ->
            if (beacons.size > 0) {
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size)
                //val firstBeacon = beacons.iterator().next()
                val firstBeacon = beacons.first()   //need to change this to read specific beacons by id

                when {
                    firstBeacon.distance < 2.0 -> {
                        beaconText.setText("You are within 2 meters of the beacon. Distance is now " + firstBeacon.distance)
                        toneGen1.startTone(ToneGenerator.TONE_PROP_PROMPT, 270);
                        vibrate(2)
                    }
                    firstBeacon.distance < 4.0 -> {
                        beaconText.setText("You are moving closer to the beacon. Distance is now " + firstBeacon.distance)
                        toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2, 270);
                        vibrate(1)
                    }
                    firstBeacon.distance < 8.0 -> {
                        beaconText.setText("You are within 10 meters of the beacon. Distance is now " + firstBeacon.distance)
                        toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
                        vibrate(0)
                    }
                    firstBeacon.distance > 8.0 -> {
                        //do something to indicate you are not near beacon anymore. maybe stop vibrate or stop beep? depending on what we choose to do
                    }
                }
            }
        }
        try {
            //beaconManager.startMonitoringBeaconsInRegion(Region("myRangingUniqueId",uniqueID))\
            // Get major and minor ID's if they exit,
            // otherwise set them to null
            var majorIdentifier: Identifier?;
            var minorIdentifier: Identifier?;
            if (currentPlace?.major_id != null) {
                majorIdentifier = Identifier.parse(Integer.valueOf(currentPlace!!.major_id).toString())
            } else {
                majorIdentifier = null;
            }
            if (currentPlace?.minor_id != null) {
                minorIdentifier = Identifier.parse(Integer.valueOf(currentPlace!!.minor_id).toString())
            } else {
                minorIdentifier = null;
            }
            // Look for beacons with the UUID, Major, and Minor.
            beaconManager.startRangingBeaconsInRegion(
                    Region(
                            "myRangingUniqueId",
                            Identifier.parse("FAB17CB9-C21C-E3B4-CD3B-D3E2E80C29FE"),
                            majorIdentifier,
                            minorIdentifier,
                    )
            )
            beaconManager.addRangeNotifier(rangeNotifier)
        } catch (e: RemoteException) {
        }
    }
    /////////////////////////////////////end beacon functions//////////////////////////////////////////////////////////////

    //we can create custom vibration patterns with this function
    private fun vibrate(vibrateMode: Int) {

        var vibratePattern = longArrayOf(0, 400, 100, 400)  //default pattern: delay 0ms, vibrate 400ms, pause 100ms, vibrate 400ms

        //0 is for largest distance, 2 is for shortest distance from beacons
        when (vibrateMode) {
            0 -> vibratePattern = longArrayOf(0, 200, 400, 200)   //sequence: delay 0ms, vibrate 200ms, pause 400ms, vibrate 200ms
            1 -> vibratePattern = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)     //sequence: delay 0ms, vibrate 100ms, pause 100ms, vibrate 100ms, pause 100ms, vibrate 100ms, pause 100ms vibrate 100ms
            2 -> vibratePattern = longArrayOf(0, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50)    //sequence: delay 0ms, vibrate 50ms, pause 50ms...
        }

        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, -1))    //use this as of api level 26
        } else {
            vibrator.vibrate(vibratePattern, -1)    //depricated function. uses this only if build api is less than 26
        }
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
        (this.applicationContext as BeaconReferenceApplication).setMonitoringActivity(null)
        beaconManager.unbind(this)
        mapView.onPause()
    }

    public override fun onResume() {
        super.onResume()
        //for beacons
        val application = this.applicationContext as BeaconReferenceApplication
        application.setMonitoringActivity(this)

        /**
         * BeaconManager bind function is necessary to start beacons ranging detection.
         * For testing and setup, leave the bind function here in onResume.
         * Once we want to start beacon ranging at the end of the navigation session,
         * comment out this bind function and uncomment the if condition in routeProgressObserver.
         * It's near the bottom. too much goddamn code in this activity
         */
        //beaconManager.bind(this)

        //for mapbox
        mapView.onResume()
        setPlaceFromIntent()
        beginNavigation()
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
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            //initializeLocationComponent(mapboxMap, it)
            locationComponent = mapboxMap.locationComponent.apply {
                activateLocationComponent(
                        LocationComponentActivationOptions.builder(
                                this@InstructionViewActivity,
                                style
                        ).build()
                )
                renderMode = RenderMode.COMPASS
                isLocationComponentEnabled = true
            }

            mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(16.0))
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

                }
                else -> restoreNavigation()
            }

            if (currentPlace != null) {
                val destinationLong = currentPlace!!.longitude.toDouble()
                val destinationLat = currentPlace!!.latitude.toDouble()
                val destinationPoint = Point.fromLngLat(destinationLong, destinationLat)
                val originLong = locationComponent!!.lastKnownLocation!!.longitude
                val originLat = locationComponent!!.lastKnownLocation!!.latitude
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
    }

    // Call this function to initiate navigation.
    @SuppressLint("MissingPermission")
    private fun beginNavigation() {
        //this task runs after a delay to ensure everything is loaded before starting nav session
        val task = Runnable {
            updateCameraOnNavigationStateChange(true)
            navigationMapboxMap?.addOnCameraTrackingChangedListener(cameraTrackingChangedListener)
            navigationMapboxMap?.addProgressChangeListener(mapboxNavigation!!)
            if (mapboxNavigation?.getRoutes()?.isNotEmpty() == true) {
                navigationMapboxMap?.startCamera(mapboxNavigation?.getRoutes()!![0])
            }
            mapboxNavigation?.startTripSession()
        }

        val handler = Handler()
        handler.postDelayed(task, 1200) //set task delay duration
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

    @SuppressLint("MissingPermission")
    private fun initListeners() {
        summaryBehavior.addBottomSheetCallback(bottomSheetCallback)

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

    private fun initViews() {

        summaryBottomSheet.visibility = GONE
        summaryBehavior = BottomSheetBehavior.from(summaryBottomSheet).apply {
            isHideable = false
        }
        recenterBtn.hide()
        routeOverviewButton = findViewById(R.id.routeOverviewBtn)
        routeOverviewButton.visibility = GONE
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
                recenterBtn.hide()
                instructionView.visibility = VISIBLE
                feedbackButton?.show()
                instructionSoundButton?.show()
                summaryBottomSheet.visibility = VISIBLE
            }
            TripSessionState.STOPPED -> {
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

            /**
             * Uncomment this if condition when you want beacon detection to start when route is completed.
             * Make sure bind function in onResume is commented out
             */
            if (routeProgress.currentState.equals(RouteProgressState.ROUTE_COMPLETE)){
                beaconManager.bind(this@InstructionViewActivity)
            }
        }
    }

    private val bannerInstructionObserver = object : BannerInstructionsObserver {
        override fun onNewBannerInstructions(bannerInstructions: BannerInstructions) {
            instructionView.updateBannerInstructionsWith(bannerInstructions)
        }
    }

    private val voiceInstructionsObserver = object : VoiceInstructionsObserver {
        override fun onNewVoiceInstructions(voiceInstructions: VoiceInstructions) {
            //remember to uncomment this for voice instructions during navigation
            speechPlayer.play(voiceInstructions)
        }
    }

    // Used to determine if the ReplayRouteLocationEngine should be used to simulate the routing.
    // This is used for testing purposes.
    private fun shouldSimulateRoute(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
                .getBoolean(this.getString(R.string.simulate_route_key), true)
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
