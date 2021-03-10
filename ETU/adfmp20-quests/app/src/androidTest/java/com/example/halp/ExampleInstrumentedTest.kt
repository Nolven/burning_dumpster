package com.example.halp

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
class ExampleInstrumentedTest {
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
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.halp", appContext.packageName)
    }
}
