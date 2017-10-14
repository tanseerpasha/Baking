package com.example.tanse.baking;

import android.test.ActivityInstrumentationTestCase2;

import com.example.tanse.baking.recipeingredients.IngredientsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static com.example.tanse.baking.TestUtils.withRecyclerView;


/**
 * Created by Tanseer on 6/13/2017.
 */

public class RecipeIngredients_RecyclerViewTest extends ActivityInstrumentationTestCase2<IngredientsActivity> {

    public RecipeIngredients_RecyclerViewTest() {
        super(IngredientsActivity.class);

    }

    @Override protected void setUp() throws Exception {
        getActivity();
    }

    public void testItemClick() {
        onView(withRecyclerView(R.id.recyclerview_recipe_ingredients).atPosition(0)).perform(click());
        //onView(withId(R.id.step_description)).check(matches(isDisplayed()));
    }


}
