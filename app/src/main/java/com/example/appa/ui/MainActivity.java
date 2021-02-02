package com.example.appa.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appa.R;
import com.example.appa.bluetooth.BluetoothServiceHandler;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.navigationlist.NavigationListActivity;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.settings.ThemeSetting;
import com.example.appa.ui.tutorial.TutorialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    public final static String LOG_TAG = MainActivity.class.getName();
    private boolean backButtonFlag = false;
    final Fragment tutorialFragment = new TutorialFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    BluetoothDialog btDialog = new BluetoothDialog();

    Fragment active = homeFragment;
    private int counter = 0;

    protected static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(MainActivity.this, BluetoothServiceHandler.class));

        setContentView(R.layout.activity_main);
        ThemeSetting.Companion.setDefaultNightModeByPreference(this);
        bindService();

        firstLaunchTutorialFrag();

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.home_button:
                    fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = homeFragment;
                    backButtonFlag = false;
                    counter = 0;
                    break;
                case R.id.settings_button:
                    //fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    fm.beginTransaction().replace(R.id.main_container, settingsFragment, "2").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = settingsFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;

                case R.id.tutorial_button:
                    fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "4").commit();
                    fm.beginTransaction().addToBackStack(null);
                    active = tutorialFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;
                case R.id.hardware_connection_button:
                    showDialog();
                    backButtonFlag = true;
                    counter = 0;
            }
            return false;
        });
        checkLocationPermissions();
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

    public void showDialog() {
        BluetoothDialog bluetoothDialog = (BluetoothDialog) BluetoothDialog.newInstance(R.string.bluetooth_title);
        btDialog = bluetoothDialog;
        btDialog.setBluetoothService(btServiceHandler);

        btDialog.show(getSupportFragmentManager(), "BTDialog");
    }

    BluetoothServiceHandler btServiceHandler;
    boolean bound;

    public void bindService() {
        Intent intent = new Intent(this, BluetoothServiceHandler.class);
        getApplicationContext().bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "Dialog bound to service");
    }

    private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothServiceHandler.LocalBinder binder = (BluetoothServiceHandler.LocalBinder) iBinder;
            btServiceHandler = binder.getService();
            Log.e(LOG_TAG, "Successfully binded");
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

}