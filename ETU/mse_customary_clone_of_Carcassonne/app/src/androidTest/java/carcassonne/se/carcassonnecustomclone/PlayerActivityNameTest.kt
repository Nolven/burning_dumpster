package carcassonne.se.carcassonnecustomclone


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class PlayerActivityNameTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun playerActivityNameTest() {
        Thread.sleep(300)
        val appCompatButton = onView(
            allOf(
                withId(R.id.playButton), withText("Play"),
                childAtPosition(
                    allOf(
                        withId(R.id.buttonsLayout),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val textView = onView(
            allOf(
                withText("Player 1"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val textView2 = onView(
            allOf(
                withText("Player 1"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView2.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.okButton), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val textView3 = onView(
            allOf(
                withText("Player 2"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView3.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.newName), withText("Player 2"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.newName), withText("Player 2"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText(""))

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.newName),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.newName),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(pressImeActionButton())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.newName),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(click())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.newName),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform(replaceText("test"), closeSoftKeyboard())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.newName), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(pressImeActionButton())

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.okButton), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())

        val textView4 = onView(
            allOf(
                withText("test"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView4.perform(click())

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())

        val textView5 = onView(
            allOf(
                withText("Player 1"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView5.perform(click())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.newName), withText("Player 1"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText8.perform(click())

        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.newName), withText("Player 1"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText9.perform(replaceText("test"))

        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.newName), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText10.perform(closeSoftKeyboard())

        val appCompatEditText11 = onView(
            allOf(
                withId(R.id.newName), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText11.perform(pressImeActionButton())

        val appCompatButton6 = onView(
            allOf(
                withId(R.id.okButton), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton6.perform(click())

        val appCompatButton7 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton7.perform(click())

        val textView6 = onView(
            allOf(
                withText("Player 1"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView6.perform(click())

        val appCompatButton8 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton8.perform(click())

        val textView7 = onView(
            allOf(
                withText("test"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView7.perform(click())

        val appCompatButton9 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton9.perform(click())

        val imageButton = onView(
            allOf(
                withId(R.id.addPlayerButton),
                childAtPosition(
                    allOf(
                        withId(R.id.playerIcons),
                        childAtPosition(
                            withId(R.id.playerArea),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        imageButton.perform(click())
        Thread.sleep(300)

        val textView8 = onView(
            allOf(
                withText("Player 2"),
                childAtPosition(
                    allOf(
                        withId(R.id.playerNames),
                        childAtPosition(
                            withId(R.id.playerArea),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        textView8.perform(click())

        val appCompatButton10 = onView(
            allOf(
                withId(R.id.cancelButton), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton10.perform(click())

        val appCompatButton11 = onView(
            allOf(
                withId(R.id.backButton), withText("Back"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton11.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
