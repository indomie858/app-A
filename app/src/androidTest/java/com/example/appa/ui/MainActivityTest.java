package com.example.appa.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.appa.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void onCreateTest() {
        try (ActivityScenario<MainActivity> mainActivityScenario = ActivityScenario.launch(MainActivity.class)) {
               mainActivityScenario.recreate();
            }
    }

    @Test
    public void openSettingsActivityTest() {
        onView(withId(R.id.settings_button)).perform(click());
    }


    @Test
    public void openNavigationListActivityTest() {
        onView(withId(R.id.directory_button)).perform(click());
    }

    @Test
    public void openBluetoothConnectActivityTest() {
        onView(withId(R.id.connect_button)).perform(click());
    }

    @Test
    public void openTutorialActivityTest() {
        onView(withId(R.id.tutorial_button)).perform(click());
    }

    @Test
    public void openAssistantActivityTest() {
        onView(withId(R.id.assistant_button)).noActivity();
    }
}