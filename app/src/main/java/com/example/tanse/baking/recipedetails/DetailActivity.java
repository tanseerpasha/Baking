package com.example.tanse.baking.recipedetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.tanse.baking.R;
import com.example.tanse.baking.Utility.Utility;
import com.example.tanse.baking.data.Contract;
import com.example.tanse.baking.recipeingredients.IngredientFragment;
import com.example.tanse.baking.recipeingredients.IngredientsActivity;
import com.example.tanse.baking.recipesteps.StepsActivity;
import com.example.tanse.baking.recipesteps.StepsFragment;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Uri mUri;
    private static final String INGREDIENTFRAGMENT_TAG = "DFTAG";
    private static final String STEPSFRAGMENT_TAG = "SFTAG";
    private static boolean mTwoPane;
    private static Bundle bundle;
    static private String recipeName;
    private String recipeMainIndex;
    private String thisActivityTitle;
    private String recipeSteps;
    private final String ACTIVITY_TILTE = "title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);

        bundle = getIntent().getExtras();
        recipeName = bundle.getString("RECIPE_MAIN_NAME");
        recipeMainIndex = bundle.getString("RECIPE_MAIN_INDEX");
        recipeSteps= bundle.getString("RECIPE_STEPS");
        thisActivityTitle = recipeName + " Details";
        Utility.fillRecipeStepsTable(this, bundle);


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ACTIVITY_TILTE)) {
                thisActivityTitle = savedInstanceState.getString(ACTIVITY_TILTE);
            }
        }

        // check for 2 pane
        if(findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                IngredientFragment ingredientFragment = new IngredientFragment();
                ingredientFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, ingredientFragment, INGREDIENTFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        this.setTitle(thisActivityTitle);
    }

    public void switchContent(Bundle bundle) {
        String stepId = bundle.getString("RECIPE_STEP_ID");
        String stepShortDescription = bundle.getString("RECIPE_SHORT_DESCRIPTION");
        bundle.putString("RECIPE_MAIN_NAME", recipeName);
        bundle.putString("RECIPE_MAIN_INDEX", recipeMainIndex);


        if(mTwoPane) {
            if(stepId.equals("-1")) {
                IngredientFragment ingredientFragment = new IngredientFragment();
                ingredientFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, ingredientFragment, INGREDIENTFRAGMENT_TAG)
                        .commit();

                thisActivityTitle = recipeName + " Ingredients";
                this.setTitle(thisActivityTitle);
            }else{

                Bundle arguments = new Bundle();
                Uri uriForSteps = Contract.RecipeDetailsEntry.buildStepsIDUri(stepId);
                arguments.putParcelable(StepsFragment.STEPS_URI,uriForSteps );
                arguments.putString(StepsFragment.STEPS_SHORT_DESCRIPTION,stepShortDescription );
                arguments.putString(StepsFragment.MAIN_RECIPE_NAME,recipeName );
                StepsFragment stepsFragment = new StepsFragment();
                stepsFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, stepsFragment, STEPSFRAGMENT_TAG)
                        .commit();
                thisActivityTitle = recipeName + " - " + stepShortDescription;
                this.setTitle(thisActivityTitle);
            }
        }else{
            if(stepId.equals("-1")){
                Toast.makeText(this, "Ingredients", Toast.LENGTH_SHORT).show();
                Intent ingredientIntent = new Intent(this, IngredientsActivity.class);
                if(bundle != null){
                    ingredientIntent.putExtras(bundle);
                }
                startActivity(ingredientIntent);
            }else{
                Toast.makeText(this, "stepId" + stepId, Toast.LENGTH_SHORT).show();

                Intent stepsIntent = new Intent(this, StepsActivity.class);
                Bundle arguments = new Bundle();
                arguments.putString("RECIPE_STEP_ID",stepId );
                arguments.putString("RECIPE_SHORT_DESCRIPTION",stepShortDescription );
                arguments.putString("RECIPE_MAIN_NAME",recipeName );
                stepsIntent.putExtras(arguments);
                startActivity(stepsIntent);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ACTIVITY_TILTE, thisActivityTitle);
        super.onSaveInstanceState(outState);
    }

    public static boolean checkTwoPane(){
        return mTwoPane;
    }
}