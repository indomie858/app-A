package com.example.appa.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.appa.R;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.*;

public class SettingsActivityTest {

    @Test
    public void onCreate() {
        try (ActivityScenario<SettingsActivity> settingsActivityScenario = ActivityScenario.launch(SettingsActivity.class)){
            settingsActivityScenario.recreate();
        }
    }

    @Test
    public void onOptionsItemSelected() {
    }

    @Test
    public void onPreferenceStartScreen() {
    }
}