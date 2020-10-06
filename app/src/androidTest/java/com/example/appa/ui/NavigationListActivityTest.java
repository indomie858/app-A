package com.example.appa.ui;

import org.junit.Test;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.appa.R;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import static org.junit.Assert.*;

public class NavigationListActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void onCreateTest() {
        try (ActivityScenario<NavigationListActivity> navigationListActivityScenario = ActivityScenario.launch(NavigationListActivity.class)) {
            navigationListActivityScenario.recreate();
        }
    }

    @Test
    public void bottomHomeNavigationbarTest() {
       onView(withId(R.id.home_button)).perform(click());
       onView(withId(R.id.settings_button)).perform(click());
       onView(withId(R.id.tutorial_button)).perform(click());
//   ----- UNCOMMENT WHEN AN ACTIVITY HAS BEEN IMPLEMENTED ------
//        onView(withId(R.id.assistant_button)).perform(click());
    }

}