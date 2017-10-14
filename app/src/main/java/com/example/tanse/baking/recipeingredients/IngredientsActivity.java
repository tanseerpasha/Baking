package com.example.tanse.baking.recipeingredients;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tanse.baking.R;

/**
 * Created by Tanseer on 6/5/2017.
 */

public class IngredientsActivity extends AppCompatActivity {

    private static final String INGREDIENTFRAGMENT_TAG = "DFTAG";
    private static final String STEPSFRAGMENT_TAG = "SFTAG";
    private static boolean mTwoPane;
    private String recipeName;
    private Bundle arguments;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_ingredients_activity);

        Bundle bundle = getIntent().getExtras();

        if(bundle == null) {
            // for espresso testing
            arguments = new Bundle();
            arguments.putString("RECIPE_MAIN_INDEX", "2");
            recipeName = "Brownies";

        }else{
            arguments = bundle;
            recipeName = arguments.getString("RECIPE_MAIN_NAME");
        }




        if (savedInstanceState == null) {
            IngredientFragment ingredientFragment = new IngredientFragment();
            ingredientFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_ingredients, ingredientFragment)
                    .commit();
        }

        this.setTitle(recipeName + " Ingredients");


    }
}
