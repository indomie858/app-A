package com.example.appa.ui.navigation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appa.R
import com.example.appa.beacons.BeaconReferenceApplication
import com.example.appa.db.PlaceEntity
import com.example.appa.viewmodel.MapWithNavViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import com.mapbox.navigation.base.trip.model.RouteLegProgress
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.base.trip.model.RouteProgressState
import com.mapbox.navigation.base.trip.model.RouteStepProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.*
import com.mapbox.navigation.ui.NavigationButton
import com.mapbox.navigation.ui.camera.NavigationCamera
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import com.mapbox.navigation.ui.summary.SummaryBottomSheet
import com.mapbox.navigation.ui.voice.NavigationSpeechPlayer
import com.mapbox.navigation.ui.voice.SpeechPlayerProvider
import com.mapbox.navigation.ui.voice.VoiceInstructionLoader
import kotlinx.android.synthetic.main.activity_directions.*
import okhttp3.Cache
import org.altbeacon.beacon.*
import java.io.File
import java.lang.ref.WeakReference
import java.lang.reflect.Executable
import java.util.*
import kotlin.math.roundToInt

/**
 * This activity combines a Mapbox navigation implementation with beacon ranging detection.
 * Main libraries used: Mapbox's Maps SDK and Navigation SDK; Android Beacon Library (Radius Networks)
 */
class DirectionsActivity :
        AppCompatActivity(),
        OnMapReadyCallback,
        BeaconConsumer {
    // SO MANY MEMBERS
    private val TAG = "DirectionsActivity"

    //members for database access
    private lateinit var viewModel: MapWithNavViewModel
    private var currentPlace: PlaceEntity? = null
    private var currentPlaceID: Int? = null
    private var majorIdentifier: Identifier? = null
    private var minorIdentifier: Identifier? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    //Mapbox member variables
    private var mapboxNavigation: MapboxNavigation? = null
    private var locationComponent: LocationComponent? = null
    private var navigationMapboxMap: NavigationMapboxMap? = null
    private lateinit var speechPlayer: NavigationSpeechPlayer
    private val mapboxReplayer = MapboxReplayer()
    private var mapboxMap: MapboxMap? = null
    private var instructionSoundButton: NavigationButton? = null
    private var directionRoute: DirectionsRoute? = null
    private var destinationName: String? = null
    private lateinit var summaryBehavior: BottomSheetBehavior<SummaryBottomSheet>
    private lateinit var routeOverviewButton: ImageButton
    private lateinit var cancelBtn: AppCompatImageButton

    //Beacon and text to speech members
    private val beaconManager = BeaconManager.getInstanceForApplication(this)
    private var ttsObject: TextToSpeech? = null

    var adapter: DirectionsAdapter? = null
    private var navigationData: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_directions)

        // Instantiate location client to get user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //verifies that device has bluetooth capabilities
        verifyBluetooth()

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
            registerVoiceInstructionsObserver(voiceInstructionsObserver)
            registerOffRouteObserver(offRouteObserver)
            registerBannerInstructionsObserver(bannerInstructionObserver)
        }

        initializeSpeechPlayer()

        //initialize TTS object... initTextChangeListener() is where tts.speak is called
        ttsObject = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                ttsObject?.setLanguage(Locale.UK)
            }
        }
        directionsActivity = this;

        //This recyclerview holds all navigation data in activity_direction.xml
        val recyclerView: RecyclerView = findViewById(R.id.directionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(directionsActivity)
        adapter = DirectionsAdapter(directionsActivity, navigationData)
        recyclerView.adapter = adapter

        //code for repeating voice instruction on floating action button click
        val myFab: FloatingActionButton = findViewById(R.id.fab_mapbox)
        myFab.setOnClickListener {
            try {
                speechPlayer.play(voiceInstruction)
            } catch (e: Exception){
                Log.e(TAG, "clicked repeat instruction before speech player had any instructions")
            }
        }

        //put actions for bottom app bar buttons here
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exit -> {
                    // Handle search icon press
                    finish()
                    true
                }
                else -> false
            }
        }
    }//end of onCreate function

    //////////////////////////////Beacon functions begin//////////////////////////////////////////
    //verifies that device is bluetooth capable and bluetooth is enabled
    private fun verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Bluetooth not enabled")
                builder.setMessage("Please enable bluetooth in settings and restart this application.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                }
                builder.show()
            }
        } catch (e: RuntimeException) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bluetooth LE not available")
            builder.setMessage("Sorry, this device does not support Bluetooth LE.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
            }
            builder.show()
        }
    }

    //Called when the beacon service is running and ready to accept your commands through the BeaconManager
    override fun onBeaconServiceConnect() {

        //Called once per second to give an estimate of the mDistance to visible beacons
        val rangeNotifier = RangeNotifier { beacons, region ->
            if (beacons.size > 0) {   //beacons is list containing all detected beacons in region
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size)
                //val firstBeacon = beacons.iterator().next()
                val firstBeacon = beacons.first()   //first detected beacon

                //updated distances are constantly being passed to atDistance()
                atDistance(firstBeacon.distance, firstBeacon, region)
            }
        }

        try {
            val region = Region(
                    //info for beacon searching
                    "myRangingUniqueId",
                    Identifier.parse("FAB17CB9-C21C-E3B4-CD3B-D3E2E80C29FE"),
                    majorIdentifier,
                    minorIdentifier,
            )
            // Look for beacons with the UUID, Major, and Minor.
            beaconManager.startRangingBeaconsInRegion(region)   //region is initialized at the top of this function
            beaconManager.addRangeNotifier(rangeNotifier)
        } catch (e: RemoteException) {
        }
    }

    //contains code to execute at various distances between user and beacon
    private fun atDistance(distance: Double, beacon: Beacon, region: Region) {
        //ToneGenerator class contains various system sounds...beeps boops and whatnot
        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        if (beacon.id2 == majorIdentifier) {
            when {
                distance < 1.5 -> {
                    try {
                        //stopping point for ranging. user has arrived at entrance
                        beaconText.text = "YOU HAVE ARRIVED AT THE ENTRANCE. PRESS BACK BUTTON TO EXIT."
                        beaconManager.stopRangingBeaconsInRegion(region)
                    } catch (e: RemoteException) {
                        Log.e(TAG, e.toString())
                    }
                }
                distance < 2.5 -> {
                    beaconText.text = "YOU ARE WITHIN 5 FEET OF THE ENTRANCE."
                    toneGen1.startTone(ToneGenerator.TONE_PROP_PROMPT, 1000);
                    vibrate(1000, 255)
                }
                distance < 4.5 -> {
                    beaconText.text = "YOU ARE WITHIN 10 FEET OF THE ENTRANCE."
                    toneGen1.startTone(ToneGenerator.TONE_PROP_PROMPT, 1000);
                    vibrate(750, 190)
                }
                distance < 6.5 -> {
                    beaconText.text = "YOU ARE WITHIN 15 FEET OF THE ENTRANCE."
                    toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2, 500);
                    vibrate(500, 127)
                }
                distance < 9.5 -> {
                    beaconText.text = "ENTRANCE LOCATED. YOU ARE WITHIN 25 FEET OF THE ENTRANCE"
                    toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP, 250);
                    vibrate(250, 63)
                }
            }
        }
    }

    /////////////////////////////////////end beacon functions//////////////////////////////////////////////////////////////
    private fun vibrate(vibeLength: Long, vibeAmplitude: Int) {
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibeLength, vibeAmplitude))
        } else {
            vibrator.vibrate(longArrayOf(0, 50), -1)    // deprecated function. uses this only if build api is less than 26
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
         * The one that actually starts the beacon ranging is in routeProgressObserver near the
         * bottom of this file. too much goddamn code in this activity.
         * The bind function here in onResume is to handle when the app loses focus.
         */
        if (isRouteComplete) {
            beaconManager.bind(this)
        }

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
            unregisterBannerInstructionsObserver(bannerInstructionObserver)
            unregisterRouteProgressObserver(routeProgressObserver)
            unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
            unregisterOffRouteObserver(offRouteObserver)
            stopTripSession()
            onDestroy()
        }

        try {
            (this.applicationContext as BeaconReferenceApplication).setMonitoringActivity(null)
            beaconManager.stopRangingBeaconsInRegion(Region(
                    "myRangingUniqueId",
                    Identifier.parse("FAB17CB9-C21C-E3B4-CD3B-D3E2E80C29FE"),
                    majorIdentifier,
                    minorIdentifier,
            ))
            beaconManager.removeAllRangeNotifiers()
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
        beaconManager.unbind(this)

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
        mapboxMap.setStyle(Style.OUTDOORS) { style ->
            locationComponent = mapboxMap.locationComponent.apply {
                activateLocationComponent(
                        LocationComponentActivationOptions.builder(
                                this@DirectionsActivity,
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
                }
                else -> restoreNavigation()
            }

            if (currentPlace != null) { //this is where we get values from the database
                try {
                    majorIdentifier = Identifier.parse(Integer.valueOf(currentPlace!!.major_id).toString())
                    minorIdentifier = Identifier.parse(Integer.valueOf(currentPlace!!.minor_id).toString())
                } catch (e: NullPointerException) {
                    Log.e(TAG, e.toString())
                }

                destinationName = currentPlace!!.name.toString()    //used for UI

                // Set up destination from current location
                val destinationLong = currentPlace!!.longitude.toDouble()
                val destinationLat = currentPlace!!.latitude.toDouble()
                val destinationPoint = Point.fromLngLat(destinationLong, destinationLat)

                // Use a simulated location if simulate route is enabled,
                // or use a real route using user's GPS location otherwise.
                // Call mapboxNaviation.requestRoutes to fetch the appropriate route.
                if (!shouldSimulateRoute()) {
                    fusedLocationClient!!.lastLocation
                            .addOnSuccessListener(this, OnSuccessListener<Location?> { location ->
                                if (location != null) {
                                    val originPoint = Point.fromLngLat(location.longitude, location.latitude)
                                    mapboxNavigation?.requestRoutes(
                                            RouteOptions.builder()
                                                    .applyDefaultParams()
                                                    .profile(RouteUrl.PROFILE_WALKING)
                                                    .accessToken(getString(R.string.mapbox_access_token))
                                                    .coordinates(listOf(originPoint, destinationPoint))
                                                    .build(), routesReqCallback
                                    )
                                }
                            })
                            .addOnFailureListener { _ ->
                                Toast.makeText(this, "No Location Found", Toast.LENGTH_SHORT).show()
                            }
                } else {
                    val originPoint = Point.fromLngLat(-118.527645, 34.2410366)
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
        handler.postDelayed(task, 3000) //set task delay duration
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

    private fun updateViews(tripSessionState: TripSessionState) {
        when (tripSessionState) {
            TripSessionState.STARTED -> {
                instructionSoundButton?.show()
            }
            TripSessionState.STOPPED -> {
                instructionSoundButton?.hide()
            }
        }
    }

    private fun updateCameraOnNavigationStateChange(navigationStarted: Boolean) {
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

    //used to compare changed on beaconText TextView
    private var previousText: String? = null

    private fun initTextChangeListener() {  //starts text change listener for beaconText. TTS will execute when text is changed
        beaconText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int, count: Int) {
                if (previousText.equals(beaconText.text.toString())) {  //if text is the same as before, dont speak TTS
                    //do nothing
                } else {
                    previousText = beaconText.text.toString()
                    ttsObject?.speak(previousText, TextToSpeech.QUEUE_FLUSH, null)  //speak changed text in beaconText
                }
            }
        })
    }

    private var isRouteComplete = false;    //flag to indicate route is complete
    var directionsActivity: DirectionsActivity? = null

    //////////////////////////MAPBOX OBSERVERS/////////////////////////////////////////////////////////////////
    /* These should be the methods that allow us to retrieve instructions and insert them into an activity */
    private val routeProgressObserver = object : RouteProgressObserver {
        override fun onRouteProgressChanged(routeProgress: RouteProgress) {
            navigationText.visibility = GONE

            val durationRemaining = (routeProgress.durationRemaining / 60).roundToInt()    //total time remaining to reach destination
            val distanceRemaining = (routeProgress.distanceRemaining * 3.281).roundToInt()  //total distance remaining to reach destination

            val currentLegProgress: RouteLegProgress? = routeProgress.currentLegProgress    //json
            val currentStepProgress: RouteStepProgress? = routeProgress.currentLegProgress?.currentStepProgress     //json

            val currentStep = currentStepProgress?.step     //arraylist or json?
            val currentName = currentStep?.name()   //name of walkway
            val currentManeuver = currentStep?.maneuver()   //arraylist or json?
            val currentInstruction = currentManeuver?.instruction()

            val upcomingStep = currentLegProgress?.upcomingStep     //arraylist or json??
            val upcomingManeuver = upcomingStep?.maneuver()     //arraylist or json?
            val upcomingManeuverType = upcomingManeuver?.type()
            val upcomingInstruction = upcomingManeuver?.instruction()

            val distanceToNextStep = (currentStepProgress?.distanceRemaining?.times(3.281))?.roundToInt()  //distance remaining in current step

            //val outputText = "$destinationName \nTotal Distance Remaining: $distanceRemaining feet. \nIn $distanceToNextStep feet, $upcomingInstruction"
            val outputText = "$destinationName,$distanceRemaining,$distanceToNextStep,$upcomingInstruction"

            var steps = routeProgress.route.legs()?.get(0)?.steps()
            navigationText.text = outputText
            //TODO do something about all this shit
            navigationData.clear()
            navigationData.add(outputText)
            if (steps != null) {
                for (step in steps) {
                    navigationData.add(step.maneuver().instruction().toString())
                }
            }
            adapter?.setData(navigationData)
            adapter?.notifyDataSetChanged()

            /**
             * This if block contains actions that execute once the user has arrived at the destination (route is complete).
             */
            if (routeProgress.currentState.equals(RouteProgressState.ROUTE_COMPLETE)) {     //executes when user has reached destination
                if (!isRouteComplete) { //this check is necessary because routeProgressObserver is constantly repeating
                    isRouteComplete = true
                    speechPlayer.isMuted = true
                    initTextChangeListener()
                    navigationTextContainer.visibility = GONE
                    beaconTextContainer.visibility = VISIBLE
                    val anim: Animation = AnimationUtils.loadAnimation(this@DirectionsActivity, R.anim.slide_in_top)
                    beaconTextContainer.startAnimation(anim)
                    beaconText.text = "YOU HAVE ARRIVED\n\nLOCATING ENTRANCE..."

                    val task = Runnable {
                        beaconManager.bind(this@DirectionsActivity)    //binds to BeaconService and starts beacon ranging
                    }
                    val handler = Handler()
                    handler.postDelayed(task, 1000) //set task delay to reduce overlap between mapbox and beacons voice
                }
            }
        }
    }

    private val bannerInstructionObserver = object : BannerInstructionsObserver {
        override fun onNewBannerInstructions(bannerInstructions: BannerInstructions){

        }
    }

    private var voiceInstruction: VoiceInstructions? = null;

    private val voiceInstructionsObserver = object : VoiceInstructionsObserver {
        override fun onNewVoiceInstructions(voiceInstructions: VoiceInstructions) {
            //remember to uncomment this for voice instructions during navigation
            speechPlayer.play(voiceInstructions)
            voiceInstruction = voiceInstructions
        }
    }

    private val offRouteObserver = object : OffRouteObserver {
        override fun onOffRouteStateChanged(offRoute: Boolean) {

        }
    }
    /////////////////////END MAPBOX OBSERVERS///////////////////////////////////////////////////////

    // Used to determine if the ReplayRouteLocationEngine should be used to simulate the routing.
    // This is used for testing purposes.
    private fun shouldSimulateRoute(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
                .getBoolean(this.getString(R.string.simulate_route_key), false);
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

    private class MyLocationEngineCallback(activity: DirectionsActivity) :
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
