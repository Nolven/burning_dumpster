package com.example.halp



import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
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
import java.lang.Thread.sleep


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SimpleLogoutTest {
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
    fun simpleLogoutTest(){
        onView(withId(R.id.navigation_profile)).perform(click())
        onView(withId(R.id.newuser_signin)).perform(click())
        onView(withId(R.id.login)).perform(typeText(emailString1))
        onView(withId(R.id.password)).perform((typeText(passwordString1)))
        onView(withId(R.id.login_login_button)).perform(click())

        onView(withId(R.id.profile_account_logout_button)).check(matches(isDisplayed()))
        onView(withId(R.id.profile_account_setting_button)).check(matches(isDisplayed()))
        onView(withId(R.id.profile_account_about_button)).check(matches(isDisplayed()))

        onView(withId(R.id.profile_account_logout_button)).perform(click())
        onView(withId(R.id.newuser_signup)).check(matches(isDisplayed()))
        onView(withId(R.id.newuser_signin)).check(matches(isDisplayed()))
    }
}
