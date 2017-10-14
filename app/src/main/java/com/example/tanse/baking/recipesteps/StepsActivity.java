package com.example.tanse.baking.recipesteps;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tanse.baking.R;
import com.example.tanse.baking.data.Contract;

/**
 * Created by Tanseer on 6/7/2017.
 */

public class StepsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_steps_activity);

        Bundle bundle = getIntent().getExtras();
        String recipeName="";
        String stepShortDescription ="";
        Bundle arguments = new Bundle();
        if(bundle == null){
            recipeName = "Brownies";
            stepShortDescription = "Recipe Introduction";
            Uri uriForSteps = Contract.RecipeDetailsEntry.buildStepsIDUri("0");
            arguments.putParcelable(StepsFragment.STEPS_URI,uriForSteps);
            arguments.putString(StepsFragment.STEPS_SHORT_DESCRIPTION, stepShortDescription);
            arguments.putString(StepsFragment.MAIN_RECIPE_NAME,recipeName );

        }else {

            recipeName = bundle.getString("RECIPE_MAIN_NAME");
            stepShortDescription = bundle.getString("RECIPE_SHORT_DESCRIPTION");

            Uri uriForSteps = Contract.RecipeDetailsEntry.buildStepsIDUri(bundle.getString("RECIPE_STEP_ID"));
            arguments.putParcelable(StepsFragment.STEPS_URI,uriForSteps );
            arguments.putString(StepsFragment.STEPS_SHORT_DESCRIPTION,stepShortDescription );
            arguments.putString(StepsFragment.MAIN_RECIPE_NAME,recipeName );

        }

        if (savedInstanceState == null) {
            StepsFragment stepsFragment = new StepsFragment();
            stepsFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_steps, stepsFragment)
                    .commit();
        }
        this.setTitle(recipeName + " - " + stepShortDescription);

    }


}
