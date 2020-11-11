package com.example.appa.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appa.R;
import com.example.appa.bluetooth.BluetoothServiceHandler;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.navigationlist.NavigationListActivity;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.settings.ThemeSetting;
import com.example.appa.ui.tutorial.TutorialFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements BluetoothDialog.BluetoothDialogListener {
    public final static String LOG_TAG = MainActivity.class.getName();
    private boolean backButtonFlag = false;
    final Fragment tutorialFragment = new TutorialFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();

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

        //fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "4").hide(tutorialFragment).commit();
        //fm.beginTransaction().replace(R.id.main_container, settingsFragment, "2").commit();
        fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();

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

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    public void onClickCardView(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, NavigationListActivity.class);
        intent.putExtra("QueryCategory", view.getContentDescription());
        context.startActivity(intent);
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
                            builder.setMessage("Please grant location access so this app can detect beacons in the background.");
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Functionality limited");
                            builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }

                            });
                            builder.show();
                        }
                    }
                }
            } else {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void showDialog() {
        DialogFragment btDialog = BluetoothDialog.newInstance(R.string.bluetooth_title);
        btDialog.show(getSupportFragmentManager(), "BTDialog");
    }

    BluetoothServiceHandler btServiceHandler;
    boolean bound;

    public void bindService()
    {
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