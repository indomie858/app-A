package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.appa.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.settings_button:
                    Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                    break;
                case R.id.tutorial_button:
                    Intent tutorialActivity = new Intent(MainActivity.this, TutorialActivity.class);
                    startActivity(tutorialActivity);
            }
            return false;
        });
    }
    //Starts Settings Activity using intent
    public void openSettingsActivity(View view) {
        Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsActivity);
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