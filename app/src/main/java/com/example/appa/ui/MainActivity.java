package com.example.appa.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.appa.R;
import com.example.appa.bluetooth.BTConnectionHelper;
import com.example.appa.bluetooth.BluetoothHandler;
import com.example.appa.bluetooth.MessageConstants;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.navigationlist.NavigationListActivity;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.settings.ThemeEnum;
import com.example.appa.ui.settings.ThemeSetting;
import com.example.appa.ui.tutorial.TutorialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Queue;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    public final static String LOG_TAG = MainActivity.class.getName();
    private boolean backButtonFlag = false;
    final Fragment tutorialFragment = new TutorialFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    BottomNavigationView bottomNavigationView;
    private BluetoothHandler bluetoothHandler; // The handler attached to the bluetooth connection
    private BTConnectionHelper btConnectionHelper;

    Fragment active = homeFragment;
    private int counter = 0;

    protected static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    TextToSpeech ttsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        ttsObject = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsObject.setLanguage(Locale.US);
                }
            }});


        bluetoothHandler = new BluetoothHandler(this);
        btConnectionHelper = new BTConnectionHelper(getApplicationContext(), bluetoothHandler);

        setContentView(R.layout.activity_main);
        ThemeSetting.Companion.setDefaultNightModeByPreference(this);
        SharedPreferences stylePref;
        stylePref = PreferenceManager.getDefaultSharedPreferences(this);
        String style = stylePref.getString("style", "");

        if(style.equals("AppTheme_Dark")){
            setTheme(R.style.AppTheme_Dark);
        }
        else if(style.equals("AppTheme_Dark_HC")){
            setTheme(R.style.AppTheme_Dark_HC);
        }
        else if(style.equals("AppTheme_Light")){
            setTheme(R.style.AppTheme_Light);
        }
        else if(style.equals("AppTheme_Light_HC")){
            setTheme(R.style.AppTheme_Light_HC);
        }
        //else setTheme(R.style.AppTheme_Dark_HC);
        firstLaunchTutorialFrag();



        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.home_button:
                    setChecked(0);
                    fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = homeFragment;
                    backButtonFlag = false;
                    counter = 0;
                    break;
                case R.id.settings_button:
                    setChecked(1);
                    fm.beginTransaction().replace(R.id.main_container, settingsFragment, "2").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = settingsFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;

                case R.id.tutorial_button:
                    setChecked(3);
                    fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "3").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = tutorialFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;
                case R.id.hardware_connection_button:
                    setChecked(2);
                    // Initiate the bluetooth discovery
                    // and thready boiz™ that manage the connection
                    if (!btConnectionHelper.isConnected) {
                        Toast.makeText(getApplicationContext(), "Discovering devices...", LENGTH_SHORT).show();
                        btConnectionHelper.appaConnect();
                    } else {
                        btConnectionHelper.terminateConnection();
                    }
            }
            return false;
        });
        checkLocationPermissions();
    }

    //set menu item checked
    private void setChecked(int id) {
        MenuItem menuItem = bottomNavigationView.getMenu().getItem(id);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            if (menuItem.isChecked()){
                menuItem.setChecked(false);
            }
        }
        menuItem.setChecked(true);
    }

    //This method checks if this is the first time the user has launched the app.
    //On first launch, this will open the tutorial fragment.
    private boolean firstLaunchTutorialFrag(){
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        boolean ranBefore = pref.getBoolean("RanBefore",false);
        if(!ranBefore){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
            fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "4").commit();
            active = tutorialFragment;
            backButtonFlag = true;
            firstLaunchMessage();
            //ThemeSetting.Companion.setDefaultNightMode(ThemeEnum.MODE_NIGHT_YES);
        }else{
            fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();
        }
        return ranBefore;
    }

    //This displays a message when user launches app for the first time.
    private void firstLaunchMessage(){
        String text = "Welcome to app-A! Since this is your first time using our app, here is a " +
                "tutorial on how to use app-A.";

        // Handler which will run after 2 seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, text, LENGTH_LONG);
                toast.show();
            }
        }, 2000);
    }

    @Override
    //  Custom back button  operation
    public void onBackPressed() {
        // when back button is pressed -- return to home
        if (backButtonFlag == true) {
            fm.beginTransaction().hide(active).replace(R.id.main_container, homeFragment, "1").commit();
            active = homeFragment;
            backButtonFlag = false;
        }

        //  requires counter to be '1' in order to exit the app
        else if (backButtonFlag == false && counter < 1) {
            String text = "Press back again to exit the app.";
            Toast toast = Toast.makeText(this, text, LENGTH_SHORT);
            toast.show();
            counter++;
        }

        //if back button is pressed 2x and they are on home page -- exit app
        else if (backButtonFlag == false && counter == 1) {
            counter = 0;
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickCardView(View view) {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)  //this checks if location permissions are granted
                == PackageManager.PERMISSION_GRANTED) {

            if (isGPSEnabled(this)) {   //checks if device GPS is turned on
                Context context = view.getContext();
                Intent intent = new Intent(context, NavigationListActivity.class);
                intent.putExtra("QueryCategory", view.getContentDescription());
                context.startActivity(intent);
            } else {    //outputs dialog when GPS is off
                GPSIsOffDialog();
            }

        } else {    //reaches this else when location permissions are denied.
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Functionality limited");
            builder.setMessage("Since location access has not been granted, navigation services are disabled. Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            builder.show();
        }
    }
    // Plays a chime sound when bluetooth sensor reading is below 30
    public void handleObjectDistance(Double objectDistance) {

        if (PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext())
                .getBoolean("isNavigating", true)) {
            if (objectDistance < 200) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setVolume(1, 1);
                    mp.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/raw/chime_bell_ding"));
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static class BluetoothHandler extends Handler {
        // Using a weak reference means the referenced class instance gets garbage collected
        // I did this because the lint was complaining
        // (That the class wasn't static, specifically).
        private  final WeakReference<MainActivity> mainActivityWeakReference;

        public BluetoothHandler(MainActivity mainActivityInstance) {
            mainActivityWeakReference = new WeakReference<MainActivity>(mainActivityInstance);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity mainActivity = mainActivityWeakReference.get();
            if (mainActivity != null) { // Null check ensures no null exceptions on mainActivity
                switch (msg.what) {
                    case (MessageConstants.MESSAGE_LOST_CONNECTION):
                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                        builder.setTitle("Disconnection Warning!").setMessage("Bluetooth device disconnected.");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        mainActivity.bottomNavigationView.getMenu().getItem(2).setTitle("Connect");
                        break;
                    case (MessageConstants.MESSAGE_CONNECTED):
                        Toast.makeText(mainActivity.getApplicationContext(), "Connection success.", LENGTH_LONG).show();
                        mainActivity.bottomNavigationView.getMenu().getItem(2).setTitle("Disconnect");
                        break;
                    case (MessageConstants.MESSAGE_DISCONNECTED):
                        Toast.makeText(mainActivity.getApplicationContext(), "Disconnected.", LENGTH_LONG).show();
                        mainActivity.bottomNavigationView.getMenu().getItem(2).setTitle("Connect");
                        break;
                    case (MessageConstants.MESSAGE_TOAST):
                        Toast.makeText(mainActivity.getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case (MessageConstants.MESSAGE_CONNECTING):
                        Toast.makeText(mainActivity.getApplicationContext(), "Connecting to device...", LENGTH_LONG).show();
                        break;
                    case (MessageConstants.MESSAGE_DATA_SENT):
                        // Use a shared preference to determine if the directions activity is active.
                        mainActivity.handleObjectDistance((Double) msg.obj);
                        break;
                }
            }
        }
    }

    //returns true if device's GPS is turned on
    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void GPSIsOffDialog() {  //dialog outputs message when gps is turned off
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Functionality limited");
        builder.setMessage("Your device's GPS is currently turned off. Please turn GPS on to use navigation service");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        builder.show();
    }


    ///////////////////////////////location permission stuff begin//////////////////////////////////
    private void locationPermissionNotGrantedDialog() {      //output dialog when location permissions are denied
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Functionality limited");
        builder.setMessage("Since location access has not been granted, navigation services are disabled.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        builder.show();
    }

    private void backgroundPermissionNotGrantedDialog() {       //output dialog when background location permissions are denied.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Functionality limited");
        builder.setMessage("Since background location access has not been granted, navigation services are disabled.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        builder.show();
    }

    public void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("This app needs background location access");
                            builder.setMessage("Please grant location access so this navigation app can function.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @TargetApi(23)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            PERMISSION_REQUEST_BACKGROUND_LOCATION);
                                }
                            });
                            builder.show();
                        } else {
                            backgroundPermissionNotGrantedDialog();
                        }
                    }
                }
            } else {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                } else {
                    locationPermissionNotGrantedDialog();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted");
                } else {
                    locationPermissionNotGrantedDialog();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted");
                } else {
                    backgroundPermissionNotGrantedDialog();
                }
                return;
            }
        }
    }
    ///////////////////////////////location permission stuff end//////////////////////////////////


}