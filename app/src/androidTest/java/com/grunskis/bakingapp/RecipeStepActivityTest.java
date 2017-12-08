package com.grunskis.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.grunskis.bakingapp.models.Step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class RecipeStepActivityTest {

    private static final String TEST_VIDEO_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";

    private static final String TEST_THUMBNAIL_URL =
            "https://www.chefbakers.com/userfiles/Dutch-Truffle38292.jpg";

    private static final Step[] TEST_ONE_STEP = new Step[]{
            new Step(0, "#1", "step 1", TEST_VIDEO_URL, "")
    };

    private static final Step[] TEST_TWO_STEPS = new Step[]{
            new Step(0, "#1", "step 1", TEST_VIDEO_URL, ""),
            new Step(1, "#2", "step 2", TEST_VIDEO_URL, "")
    };

    private static final Step[] TEST_ONE_STEP_IMAGE = new Step[]{
            new Step(0, "#1", "step 1", "", TEST_THUMBNAIL_URL)
    };
    @Rule
    public ActivityTestRule<RecipeStepActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipeStepActivity.class, true, false);
    private Intent mIntent;

    @Before
    public void setIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mIntent = new Intent(targetContext, RecipeStepActivity.class);
        mIntent.putExtra(RecipeStepActivity.EXTRA_RECIPE_NAME, "TestRecipe");
    }

    @Test
    public void phonePortrait_buttonsDisabledWithSingleStep() {
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEPS, TEST_ONE_STEP);
        mActivityTestRule.launchActivity(mIntent);

        onView(withId(R.id.b_previous_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));
        onView(withId(R.id.b_next_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));
    }

    @Test
    public void phonePortrait_nextButtonEnabled() {
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEPS, TEST_TWO_STEPS);
        mActivityTestRule.launchActivity(mIntent);

        onView(withId(R.id.b_previous_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));
        onView(withId(R.id.b_next_step)).check(matches(
                allOf(isDisplayed(), isEnabled())));

        onView(withId(R.id.b_next_step)).perform(click());
        onView(withId(R.id.b_previous_step)).check(matches(
                allOf(isDisplayed(), isEnabled())));
        onView(withId(R.id.b_next_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));
    }

    @Test
    public void phonePortrait_previousButtonEnabled() {
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEPS, TEST_TWO_STEPS);
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEP_INDEX, 1);
        mActivityTestRule.launchActivity(mIntent);

        onView(withId(R.id.b_previous_step)).check(matches(
                allOf(isDisplayed(), isEnabled())));
        onView(withId(R.id.b_next_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));

        onView(withId(R.id.b_previous_step)).perform(click());
        onView(withId(R.id.b_previous_step)).check(matches(
                allOf(isDisplayed(), not(isEnabled()))));
        onView(withId(R.id.b_next_step)).check(matches(
                allOf(isDisplayed(), isEnabled())));
    }

    @Test
    public void phoneLandscape_showOnlyVideo() throws RemoteException {
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEPS, TEST_ONE_STEP);
        mActivityTestRule.launchActivity(mIntent);

        onView(allOf(
                withId(R.id.player_view),
                withClassName(is(SimpleExoPlayerView.class.getName())))).check(
                matches(isDisplayed()));
        onView(withId(R.id.iv_step_thumbnail)).check(matches(not(isDisplayed())));
        onView(withId(R.id.b_previous_step)).check(matches(isDisplayed()));
        onView(withId(R.id.b_next_step)).check(matches(isDisplayed()));

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.setOrientationLeft();

        onView(allOf(
                withId(R.id.player_view),
                withClassName(is(SimpleExoPlayerView.class.getName())))).check(
                matches(isDisplayed()));
        onView(withId(R.id.b_previous_step)).check(doesNotExist());
        onView(withId(R.id.b_next_step)).check(doesNotExist());
    }

    @Test
    public void phoneLandscape_showOnlyImage() throws RemoteException {
        mIntent.putExtra(RecipeStepActivity.EXTRA_STEPS, TEST_ONE_STEP_IMAGE);
        mActivityTestRule.launchActivity(mIntent);

        onView(withId(R.id.iv_step_thumbnail)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.player_view),
                withClassName(is(SimpleExoPlayerView.class.getName())))).check(
                matches(not(isDisplayed())));
        onView(withId(R.id.b_previous_step)).check(matches(isDisplayed()));
        onView(withId(R.id.b_next_step)).check(matches(isDisplayed()));

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.setOrientationLeft();

        onView(withId(R.id.iv_step_thumbnail)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.player_view),
                withClassName(is(SimpleExoPlayerView.class.getName())))).check(
                matches(not(isDisplayed())));
        onView(withId(R.id.b_previous_step)).check(doesNotExist());
        onView(withId(R.id.b_next_step)).check(doesNotExist());
    }

}
