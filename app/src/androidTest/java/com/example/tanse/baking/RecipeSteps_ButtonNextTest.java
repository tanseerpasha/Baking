package com.example.tanse.baking;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.tanse.baking.recipesteps.StepsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Tanseer on 6/13/2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeSteps_ButtonNextTest {
    @Rule public ActivityTestRule<StepsActivity> mActivityTestRule
            = new ActivityTestRule<StepsActivity>(StepsActivity.class);

    @Test
    public void clickNextButton_ChangeRecipeStep(){
        onView(withId(R.id.buttonNext)).perform(click());

        onView(withId(R.id.step_description)).check(matches(isDisplayed()));

    }
}
