package com.example.appa.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.appa.R;
import com.example.appa.ui.navigationlist.NavigationListActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class NavigationListActivityTest {

    @Rule
    public ActivityScenarioRule<NavigationListActivity> activityRule
            = new ActivityScenarioRule<>(NavigationListActivity.class);

    @Test
    public void onCreateTest() {
        try (ActivityScenario<NavigationListActivity> navigationListActivityScenario = ActivityScenario.launch(NavigationListActivity.class)) {
            navigationListActivityScenario.recreate();
        }
    }

    /*@Test
    public void openMainActivityTest() {
        onView(withId(R.id.home_button)).perform(click());
    }*/

    @Test
    public void openTutorialsActivityTest() {
        onView(withId(R.id.tutorial_button)).perform(click());
    }

    @Test
    public void openSettingsActivityTest(){
        onView(withId(R.id.settings_button)).perform(click());
    }

    /*@Test
    public void openAssistantActivityTest() {
        onView(withId(R.id.assistant_button)).noActivity();
    }*/

}