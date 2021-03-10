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
class MenuActivitiesTest {
    val WAIT = 300L

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun mainMenuActivityTest() {
        Thread.sleep(WAIT)
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
        appCompatButton2.perform(click())

        val appCompatButton3 = onView(
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
        appCompatButton3.perform(click())

        val appCompatButton4 = onView(
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
        appCompatButton4.perform(click())

        val appCompatImageButton = onView(
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
        appCompatImageButton.perform(click())

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

        val appCompatImageButton2 = onView(
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
        appCompatImageButton2.perform(click())

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
                withId(R.id.exitButton), withText("Exit"),
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
        appCompatButton7.perform(click())

        val appCompatButton8 = onView(
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
        appCompatButton8.perform(click())

        val appCompatButton9 = onView(
            allOf(
                withId(R.id.exitButton), withText("Exit"),
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
        appCompatButton9.perform(click())

        val appCompatButton10 = onView(
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
        appCompatButton10.perform(click())
    }

    @Test
    fun playerActivityTest() {
        Thread.sleep(WAIT)
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
                childAtPosition(
                    allOf(
                        withId(R.id.playerIcons),
                        childAtPosition(
                            withId(R.id.playerArea),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageButton.perform(click())

        val imageButton2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.playerIcons),
                        childAtPosition(
                            withId(R.id.playerArea),
                            0
                        )
                    ),
                    1
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
                    2
                ),
                isDisplayed()
            )
        )
        imageButton3.perform(click())

        val appCompatButton2 = onView(
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
        appCompatButton2.perform(click())
    }

    @Test
    fun settingsActivityTest() {
        Thread.sleep(WAIT)
        val appCompatImageButton = onView(
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
        appCompatImageButton.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.effectsPlus),
                childAtPosition(
                    allOf(
                        withId(R.id.settingsArea),
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
        appCompatImageButton2.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.effectsMinus),
                childAtPosition(
                    allOf(
                        withId(R.id.settingsArea),
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
        appCompatImageButton3.perform(click())

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.musicPlus),
                childAtPosition(
                    allOf(
                        withId(R.id.settingsArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatImageButton4.perform(click())

        val appCompatImageButton5 = onView(
            allOf(
                withId(R.id.musicMinus),
                childAtPosition(
                    allOf(
                        withId(R.id.settingsArea),
                        childAtPosition(
                            withClassName(`is`("android.support.constraint.ConstraintLayout")),
                            1
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatImageButton5.perform(click())

        val appCompatButton = onView(
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
        appCompatButton.perform(click())
    }

    @Test
    fun rulesActivityTest() {
        Thread.sleep(WAIT)
        val appCompatButton = onView(
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
        appCompatButton.perform(click())

        val appCompatTextView = onView(
            allOf(
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
        appCompatTextView.perform(click())

        val appCompatButton2 = onView(
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
        appCompatButton2.perform(click())
    }

    @Test
    fun infoActivityTest() {
        Thread.sleep(WAIT)
        val appCompatImageButton = onView(
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
        appCompatImageButton.perform(click())

        val appCompatTextView = onView(
            allOf(
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
        appCompatTextView.perform(click())

        val appCompatButton = onView(
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
        appCompatButton.perform(click())
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
