package com.example.tanse.baking.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.tanse.baking.R;
import com.example.tanse.baking.data.Contract;
import com.example.tanse.baking.sync.SyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by tanse on 5/17/2017.
 */

public class Utility {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return
     */

    private static int  recipeStepsCount = 0;
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void setRecipeStepsCount(int count){
        recipeStepsCount = count;
    }

    public static int getRecipeStepsCount(){
        return recipeStepsCount;
    }



    @SuppressWarnings("ResouceType")
    static public @SyncAdapter.RecipeServerStatus
    int getRecipeServerStaus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_recipe_status_key), SyncAdapter.RECIPE_STATUS_UNKNOWN);
    }

    static public void setVideoUrl(Context context, String videoUrl){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(context.getString(R.string.pref_video_url_key),videoUrl );
        spe.commit();

    }

    static public String getVideoUrl(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_video_url_key), null);
    }

    static public String getRecipeNameForIngredientWidget(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_recipe_name_key), "Nutella Pie");
    }

    static public String getRecipeIdForIngredientWidget(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_recipe_id_key), "1");
    }


    public static void fillRecipeStepsTable(Context context, Bundle bundle) {
        String recipeSteps;

        recipeSteps = bundle.getString("RECIPE_STEPS");
        final String STEPS_ID = "id";
        final String STEPS_SHORTDESCRIPTION = "shortDescription";
        final String STEPS_DESCRIPTION = "description";
        final String STEPS_VIDEOURL = "videoURL";
        final String STEPS_THUMBNAILURL = "thumbnailURL";
        try {
            JSONArray recipeDetailsArray = new JSONArray(recipeSteps);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(recipeDetailsArray.length());

            for(int i = -1; i< recipeDetailsArray.length();i++){
                ContentValues stepsValues = new ContentValues();
                if(i==-1){
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_ID, "-1");
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_SHORTDESCRIPTION, "Ingredients");
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_DESCRIPTION, "");
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_VIDEOURL, "");
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_THUMBNAILURL, "");
                }else {
                    JSONObject recipeStepDetails = recipeDetailsArray.getJSONObject(i);
                    String stepId = recipeStepDetails.getString(STEPS_ID);
                    String stepShortDescription = recipeStepDetails.getString(STEPS_SHORTDESCRIPTION);
                    String stepDescription = recipeStepDetails.getString(STEPS_DESCRIPTION);
                    String stepVideoURL = recipeStepDetails.getString(STEPS_VIDEOURL);
                    String stepThumbnailURL = recipeStepDetails.getString(STEPS_THUMBNAILURL);
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_ID, stepId);
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_SHORTDESCRIPTION, stepShortDescription);
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_DESCRIPTION, stepDescription);
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_VIDEOURL, stepVideoURL);
                    stepsValues.put(Contract.RecipeDetailsEntry.COLUMN_STEP_THUMBNAILURL, stepThumbnailURL);

                }
                cVVector.add(stepsValues);
            }

            int inserted = 0;
            if( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                context.getContentResolver().delete(Contract.RecipeDetailsEntry.CONTENT_URI,null,null);
                int recipeStepsCount = context.getContentResolver().bulkInsert(Contract.RecipeDetailsEntry.CONTENT_URI, cvArray);
                Utility.setRecipeStepsCount(recipeStepsCount);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
