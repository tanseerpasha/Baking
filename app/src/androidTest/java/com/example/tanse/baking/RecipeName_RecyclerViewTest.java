package com.example.tanse.baking;

import android.test.ActivityInstrumentationTestCase2;

import com.example.tanse.baking.recipenames.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.example.tanse.baking.TestUtils.withRecyclerView;


/**
 * Created by Tanseer on 6/13/2017.
 */

public class RecipeName_RecyclerViewTest extends ActivityInstrumentationTestCase2<MainActivity > {



    public RecipeName_RecyclerViewTest() {
        super(MainActivity.class);
    }

    @Override protected void setUp() throws Exception {
        getActivity();
    }

    public void testItemClick() {
        onView(withRecyclerView(R.id.recyclerview_recipe_names).atPosition(1)).perform(click());

        //onView(withId(R.id.recipe_detail_info)).check(matches(isDisplayed()));

        onView(withRecyclerView(R.id.recyclerview_recipe_details).atPosition(5)).check(matches(isDisplayed()));
    }


}
