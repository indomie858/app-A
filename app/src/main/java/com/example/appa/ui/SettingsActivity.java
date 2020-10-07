package com.example.appa.ui;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceScreen;

import com.example.appa.R;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Toolbar setting
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment.newInstance("preference_root"))
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // ActionBar arrow, push to back
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        // switch Fragment、back to fragment, with addToBackStack
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment.newInstance(pref.getKey()))
                .addToBackStack(null)
                .commit();
        return true;
    }
}
