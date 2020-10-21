package com.example.appa.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appa.R;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.tutorial.TutorialFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BluetoothDialog.BluetoothDialogListener {

    final Fragment tutorialFragment = new TutorialFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar actionbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        fm.beginTransaction().add(R.id.main_container, tutorialFragment, "4").hide(tutorialFragment).commit();
        fm.beginTransaction().add(R.id.main_container, settingsFragment, "2").hide(settingsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").commit();

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.home_button:
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    actionbar.setTitle("Home");
                    active = homeFragment;
                    break;
                case R.id.settings_button:
                    fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    actionbar.setTitle("Settings");
                    active = settingsFragment;
                    break;
                case R.id.tutorial_button:
                    fm.beginTransaction().hide(active).show(tutorialFragment).commit();
                    actionbar.setTitle("Tutorial");
                    active = tutorialFragment;
                    break;
                case R.id.assistant_button:
            }
            return false;
        });
    }
    public void bluetoothSetupPopup() {
        //DialogFragment bluetoothDialog = new BluetoothDialog();
        //bluetoothDialog.show(getSupportFragmentManager(), "Bluetooth");
        startActivity(btDialog);
        Intent btDialog = new Intent(MainActivity.this, BluetoothDialog.class);
    }
    @Override

    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }
}