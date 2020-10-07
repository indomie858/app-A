package com.example.appa.ui;

import androidx.test.core.app.ActivityScenario;

import com.example.appa.ui.tutorial.TutorialActivity;

import org.junit.Test;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.appa.R;
import org.junit.Rule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.*;

public class TutorialActivityTest {
    @Rule
    public ActivityScenarioRule<TutorialActivity> activityRule
            = new ActivityScenarioRule<>(TutorialActivity.class);

    @Test
    public void onCreate() {
        try (ActivityScenario<TutorialActivity> settingsActivityScenario = ActivityScenario.launch(TutorialActivity.class)){
            settingsActivityScenario.recreate();
        }
    }
    @Test
    public void openMainActivityTest() {
        onView(withId(R.id.home_button)).perform(click());
    }

    @Test
    public void openTutorialsActivityTest() {
        onView(withId(R.id.tutorial_button)).noActivity();
    }

    @Test
    public void openSettingsActivityTest(){
        onView(withId(R.id.settings_button)).perform(click());
    }

    @Test
    public void openAssistantActivityTest() {
        onView(withId(R.id.assistant_button)).noActivity();
    }
}