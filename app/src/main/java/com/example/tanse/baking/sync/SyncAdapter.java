package com.example.tanse.baking.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanse.baking.R;
import com.example.tanse.baking.data.Contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Vector;


/**
 * Created by tanse on 6/3/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private  final String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "com.example.tanse.baking";
    private final Context mContext;
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private String recipeId;
    private String recipeName;
    private String recipeIngredients;
    private String recipeSteps;
    private String recipeServings;
    private int recipeIngredientslength = 0;
    Vector<ContentValues> cVVector1 = new Vector<ContentValues>();


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RECIPE_STATUS_OK, RECIPE_STATUS_SERVER_DOWN, RECIPE_STATUS_UNKNOWN,RECIPE_STATUS_SERVER_INVALID})
    public  @interface RecipeServerStatus{}
    public static final int RECIPE_STATUS_OK = 0;
    public static final int RECIPE_STATUS_SERVER_DOWN = 1;
    public static final int RECIPE_STATUS_UNKNOWN = 2;
    public static final int RECIPE_STATUS_SERVER_INVALID = 3;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String baseUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getContext());

        //JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, baseUrl,
        JsonArrayRequest request = new JsonArrayRequest(baseUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            getRecipeDataFromJson(response);
                        }catch (JSONException e){
                            e.printStackTrace();
                            setRecipeServerStatus(getContext(), RECIPE_STATUS_SERVER_INVALID);

                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                setRecipeServerStatus(getContext(), RECIPE_STATUS_SERVER_DOWN);


            }
        });
        requestQueue.add(request);
    }

    private void getRecipeDataFromJson(JSONArray recipeJsonArray)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RECIPE_ID = "id";
        final String RECIPE_NAME = "name";
        final String RECIPE_INGREDIENTS = "ingredients";
        final String RECIPE_STEPS = "steps";
        final String RECIPE_SERVINGS = "servings";



        try {

            Vector<ContentValues> cVVector = new Vector<ContentValues>(recipeJsonArray.length());
            cVVector1.clear();


            for (int i = 0; i < recipeJsonArray.length(); i++) {
                JSONObject recipeNameObject = recipeJsonArray.getJSONObject(i);
                recipeId = recipeNameObject.getString(RECIPE_ID);
                recipeName = recipeNameObject.getString(RECIPE_NAME);
                recipeIngredients = recipeNameObject.getString(RECIPE_INGREDIENTS);
                recipeSteps = recipeNameObject.getString(RECIPE_STEPS);
                recipeServings = recipeNameObject.getString(RECIPE_SERVINGS);

                try {
                    fillRecipeIngredientsTable(recipeIngredients, recipeId);
                }catch (JSONException e){
                    e.printStackTrace();
                }


                ContentValues recipeValues = new ContentValues();

                recipeValues.put(Contract.RecipeNameEntry.COLUMN_RECIPE_ID, recipeId);
                recipeValues.put(Contract.RecipeNameEntry.COLUMN_RECIPE_NAME, recipeName);
                recipeValues.put(Contract.RecipeNameEntry.COLUMN_RECIPE_INGREDIENTS, recipeIngredients);
                recipeValues.put(Contract.RecipeNameEntry.COLUMN_RECIPE_STEPS, recipeSteps);
                recipeValues.put(Contract.RecipeNameEntry.COLUMN_RECIPE_SERVINGS, recipeServings);

                cVVector.add(recipeValues);

            }

            int inserted = 0;
            if( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(Contract.RecipeNameEntry.CONTENT_URI,null,null);
                getContext().getContentResolver().bulkInsert(Contract.RecipeNameEntry.CONTENT_URI, cvArray);

                updateWidgets();
            }

            //Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
            setRecipeServerStatus(getContext(), RECIPE_STATUS_OK);
            int ingrdientsInserted = 0;
            if( cVVector1.size() > 0 ) {
                ContentValues[] cvArray1 = new ContentValues[cVVector1.size()];
                cVVector1.toArray(cvArray1);

                getContext().getContentResolver().delete(Contract.RecipeIngredientsEntry.CONTENT_URI,null,null);
                getContext().getContentResolver().bulkInsert(Contract.RecipeIngredientsEntry.CONTENT_URI, cvArray1);
            }
            //Log.d(LOG_TAG, "Sync Completetest. " + cVVector1.size() + " Inserted");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setRecipeServerStatus(getContext(), RECIPE_STATUS_SERVER_INVALID);
        }
    }

    private void updateWidgets(){
        Context context = getContext();
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */

        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void fillRecipeIngredientsTable(String sRecipeIngredients, String sRecipeId)
            throws JSONException {

        JSONArray recipeIngredientsArray;
        String ingredientName;
        String ingredientQuanity;
        String ingredientMeasure;
        final String INGREDIENT_NAME = "ingredient";
        final String INGREDIENT_QUANTITY = "quantity";
        final String INGREDIENT_MEASURE = "measure";


        try {


            recipeIngredientsArray = new JSONArray(sRecipeIngredients);

            for(int j = -1; j< recipeIngredientsArray.length();j++){
                //ContentValues ingredientValues = new ContentValues();
                ContentValues recipeIngredientValues = new ContentValues();

                if(j == -1){
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID, sRecipeId);
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME, "Item Name");
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_QUANTITY, "Quantity  ");
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_MEASURE, "Measure");
                }else {
                    JSONObject ingredientNames = recipeIngredientsArray.getJSONObject(j);
                    ingredientName = ingredientNames.getString(INGREDIENT_NAME);
                    ingredientQuanity = ingredientNames.getString(INGREDIENT_QUANTITY);
                    ingredientMeasure = ingredientNames.getString(INGREDIENT_MEASURE);

                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID, sRecipeId);
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME, ingredientName);
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_QUANTITY, ingredientQuanity);
                    recipeIngredientValues.put(Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_MEASURE, ingredientMeasure);

                    //cVVector1.add(recipeIngredientValues);
                }

                cVVector1.add(recipeIngredientValues);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }



    }

    static private void setRecipeServerStatus(Context context, @RecipeServerStatus int recipeServerStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_recipe_status_key), recipeServerStatus);
        spe.apply();

    }


}
