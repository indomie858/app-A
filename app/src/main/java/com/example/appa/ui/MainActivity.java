package com.example.appa.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.appa.R;
import com.example.appa.ui.home.HomeFragment;
import com.example.appa.ui.navigationlist.NavigationListActivity;
import com.example.appa.ui.settings.SettingsFragment;
import com.example.appa.ui.settings.ThemeSetting;
import com.example.appa.ui.tutorial.TutorialFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements BluetoothDialog.BluetoothDialogListener {

    private boolean backButtonFlag = false;
    final Fragment tutorialFragment = new TutorialFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment homeFragment = new HomeFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment active = homeFragment;
    MaterialToolbar actionbar;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeSetting.Companion.setDefaultNightModeByPreference(this);

        actionbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        //fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "4").hide(tutorialFragment).commit();
        //fm.beginTransaction().replace(R.id.main_container, settingsFragment, "2").commit();
        fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.home_button:
                    fm.beginTransaction().replace(R.id.main_container, homeFragment, "1").commit();
                    fm.beginTransaction().addToBackStack(null);
                    actionbar.setTitle("Home");
                    active = homeFragment;
                    backButtonFlag = false;
                    counter = 0;
                    break;
                case R.id.settings_button:
                    //fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    fm.beginTransaction().replace(R.id.main_container, settingsFragment, "2").commit();
                    fm.beginTransaction().addToBackStack(null);
                    actionbar.setTitle("Settings");
                    active = settingsFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;

                case R.id.tutorial_button:
                    fm.beginTransaction().replace(R.id.main_container, tutorialFragment, "4").commit();
                    fm.beginTransaction().addToBackStack(null);
                    actionbar.setTitle("Tutorial");
                    active = tutorialFragment;
                    backButtonFlag = true;
                    counter = 0;
                    break;
                case R.id.hardware_connection_button:
                    backButtonFlag = true;
                    counter = 0;
            }
            return false;
        });
    }

    @Override
    //  Custom back button  operation
    public void onBackPressed() {

        // when back button is pressed -- return to home
        if (backButtonFlag == true){
            fm.beginTransaction().hide(active).replace(R.id.main_container, homeFragment, "1").commit();
            actionbar.setTitle("Home");
            active = homeFragment;
            backButtonFlag = false;
        }

        //  requires counter to be '1' in order to exit the app
        else if (backButtonFlag == false && counter <1){
            String text = "Press back again to exit the app.";
            Toast toast = Toast.makeText(this, text, LENGTH_SHORT);
            toast.show();
            counter++;
        }

        //if back button is pressed 2x and they are on home page -- exit app
        else if (backButtonFlag == false && counter == 1){
            counter = 0;
            finish();
        }
    }



    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

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

}