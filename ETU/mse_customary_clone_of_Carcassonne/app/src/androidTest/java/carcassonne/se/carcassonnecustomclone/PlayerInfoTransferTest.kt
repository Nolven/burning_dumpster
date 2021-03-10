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
class PlayerInfoTransferTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun playerInfoTransferTest() {
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

        val imageButton2 = onView(
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
                    3
                ),
                isDisplayed()
            )
        )
        imageButton2.perform(click())

        val imageButton3 = onView(
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
                    4
                ),
                isDisplayed()
            )
        )
        imageButton3.perform(click())

        val imageButton4 = onView(
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
                    5
                ),
                isDisplayed()
            )
        )
        imageButton4.perform(click())

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

        val playerGameInfo = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        playerGameInfo.perform(click())

        val playerGameInfo2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        playerGameInfo2.perform(click())

        val playerGameInfo3 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        playerGameInfo3.perform(click())

        val playerGameInfo4 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        playerGameInfo4.perform(click())

        val playerGameInfo5 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        playerGameInfo5.perform(click())

        val playerGameInfo6 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerInfoArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            4
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        playerGameInfo6.perform(click())

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

        val appCompatButton3 = onView(
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
        appCompatButton3.perform(click())

        val appCompatButton4 = onView(
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
        appCompatButton4.perform(click())
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
