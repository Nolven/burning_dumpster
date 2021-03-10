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
class PlayerActivityAddDeleteTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun playerActivityAddDeleteTest() {
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

        val imageButton5 = onView(
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
        imageButton5.perform(click())

        val imageButton6 = onView(
            allOf(
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
        imageButton6.perform(click())

        val imageButton7 = onView(
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
        imageButton7.perform(click())

        val imageButton8 = onView(
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
        imageButton8.perform(click())

        val imageButton9 = onView(
            allOf(
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
        imageButton9.perform(click())

        val imageButton10 = onView(
            allOf(
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
        imageButton10.perform(click())

        val imageButton11 = onView(
            allOf(
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
        imageButton11.perform(click())

        val imageButton12 = onView(
            allOf(
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
        imageButton12.perform(click())

        val imageButton13 = onView(
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
        imageButton13.perform(click())

        val imageButton14 = onView(
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
        imageButton14.perform(click())

        val imageButton15 = onView(
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
        imageButton15.perform(click())

        val imageButton16 = onView(
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
        imageButton16.perform(click())
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
