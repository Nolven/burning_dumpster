package com.example.halp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith

import org.junit.Assert.*

import org.junit.Before
import androidx.test.rule.ActivityTestRule


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ClickOnEveryNavTest {
    private lateinit var emailString1: String
    private lateinit var passwordString1: String

    private lateinit var emailString: String
    private lateinit var passwordString: String
    private lateinit var nameString: String
    private lateinit var phoneString: String

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        // Specify a valid string.



        emailString1 = "Fizmatinfo@yandex.ru"
        passwordString1 = "12345"

        emailString = "inglordlear@gmail.com"
        passwordString = "1"
        nameString = "Yaroslav"
        phoneString = "79818212203"
    }

    @Test
    fun clickOnEveryNavTest(){
        onView(withId(R.id.navigation_favourites)).perform(click())
        onView(withId(R.id.textView)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_map)).perform(click())

        onView(withId(R.id.navigation_orders)).perform(click())
        onView(withId(R.id.textView)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_search)).perform(click())
        onView(withId(R.id.navigation_profile)).perform(click())

    }

}