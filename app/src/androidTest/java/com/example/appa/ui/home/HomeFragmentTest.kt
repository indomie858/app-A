package com.example.appa.ui.home


import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.appa.R
import com.example.appa.ui.MainActivity
import org.junit.Test

class HomeFragmentTest {

    @Test
    fun homeFragmentTest() {
        //SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.connect_button)).check(matches(isDisplayed()))
        onView(withId(R.id.directory_button)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_button)).check(matches(isDisplayed()))

    }

    @Test
    fun homeButtonTest() {
        //SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.home_button)).perform(click())
    }

    @Test
    fun settingsButtonTest() {
        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // VERIFY
        onView(withId(R.id.settings_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun tutorialButtonTest() {
        //SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // VERIFY
        onView(withId(R.id.tutorial_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun assistantButtonTest() {
        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.assistant_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBluetoothButtonTest() {

        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // VERIFY
        onView(withId(R.id.connect_button)).check(matches(isDisplayed()))

        // ACTION
        onView(withId(R.id.connect_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateDirectoryButtonTest() {

        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // VERIFY
        onView(withId(R.id.directory_button)).check(matches(isDisplayed()))

        // ACTION
        onView(withId(R.id.directory_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateNavigationButtonTest() {

        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // VERIFY
        onView(withId(R.id.navigation_button)).check(matches(isDisplayed()))

        // ACTION
        onView(withId(R.id.navigation_button)).perform(click())

        // NAVIGATE
        pressBack()

        // VERIFY
        onView(withId(R.id.homeFragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun exitAppTest() {
        // SETUP
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        // EXIT APP
        pressBack()

    }
}