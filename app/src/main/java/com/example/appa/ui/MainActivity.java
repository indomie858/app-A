package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appa.R;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.navigationlist.NavigationListActivity;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.tutorial.TutorialActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.main_container, settingsFragment, "2").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").commit();

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.home_button:
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    break;
                case R.id.settings_button:
                    /*Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsActivity);*/
                    fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment;
                    break;
                case R.id.tutorial_button:
                    Intent tutorialActivity = new Intent(MainActivity.this, TutorialActivity.class);
                    startActivity(tutorialActivity);
                    break;
                case R.id.assistant_button:

            }
            return false;
        });
    }


    //Starts Map Activity using intent
    public void openMapActivity(View view) {
        Intent mapActivity = new Intent(MainActivity.this, MapActivity.class);
        startActivity(mapActivity);
    }

    //Starts Navigation list activity using intent
    public void openNavigationListActivity(View view) {
        Intent navigationListActivity = new Intent(MainActivity.this, NavigationListActivity.class);
        startActivity(navigationListActivity);
    }

    //Starts Bluetooth Connect activity using intent
    public void openBluetoothConnectActivity(View view) {
        Intent bluetoothConnectActivity = new Intent(MainActivity.this, BluetoothConnectAcitvity.class);
        startActivity(bluetoothConnectActivity);
    }
}