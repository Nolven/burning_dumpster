package carcassonne.se.carcassonnecustomclone



import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class GameActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun gameActivityTest() {
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

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.playButton), withText("Play"),
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

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(50)

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.remainingTiles),
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

        val appCompatButton4 = onView(
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
        appCompatButton4.perform(click())

        val currentTile = onView(
            allOf(
                withId(R.id.currentTileView),
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
        currentTile.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.pauseButton),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())
        Thread.sleep(300)

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.settingsButton),
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
        appCompatImageButton2.perform(click())

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.backButton), withText("Back"),
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

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.infoButton),
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
        appCompatImageButton3.perform(click())

        val appCompatButton6 = onView(
            allOf(
                withId(R.id.backButton), withText("Back"),
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
        appCompatButton6.perform(click())

        val appCompatButton7 = onView(
            allOf(
                withId(R.id.rulesButton), withText("Rules"),
                childAtPosition(
                    allOf(
                        withId(R.id.buttonsLayout),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton7.perform(click())

        val appCompatButton8 = onView(
            allOf(
                withId(R.id.backButton), withText("Back"),
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

        val appCompatButton9 = onView(
            allOf(
                withId(R.id.resumeButton), withText("Resume"),
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
        appCompatButton9.perform(click())

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.pauseButton),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton4.perform(click())

        val appCompatButton10 = onView(
            allOf(
                withId(R.id.resumeButton), withText("Resume"),
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
        appCompatButton10.perform(click())

        val appCompatImageButton5 = onView(
            allOf(
                withId(R.id.pauseButton),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton5.perform(click())

        val appCompatButton11 = onView(
            allOf(
                withId(R.id.exitButton), withText("Exit to menu"),
                childAtPosition(
                    allOf(
                        withId(R.id.buttonsLayout),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton11.perform(click())

        val appCompatButton12 = onView(
            allOf(
                withId(R.id.noButton), withText("No"),
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
        appCompatButton12.perform(click())

        val appCompatButton13 = onView(
            allOf(
                withId(R.id.exitButton), withText("Exit to menu"),
                childAtPosition(
                    allOf(
                        withId(R.id.buttonsLayout),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton13.perform(click())

        val appCompatButton14 = onView(
            allOf(
                withId(R.id.yesButton), withText("Yes"),
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
        appCompatButton14.perform(click())
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
