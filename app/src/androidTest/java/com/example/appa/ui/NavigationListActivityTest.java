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

}