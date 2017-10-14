package com.example.tanse.baking.recipenames;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tanse.baking.R;
import com.example.tanse.baking.Utility.Utility;
import com.example.tanse.baking.data.Contract;
import com.example.tanse.baking.recipedetails.DetailActivity;
import com.example.tanse.baking.sync.SyncAdapter;

import org.json.JSONArray;

/**
 * Created by tanse on 5/17/2017.
 */

public class NameFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        NameAdapter.NameAdapterOnClickLHandler
{

    private static final String LOG_TAG = NameFragment.class.getSimpleName();

    // For the recipe view we're showing only a names from the stored data.
    // Specify the columns we need
    private static final String[] RECIPE_COLUMNS_PROJECTION = {    //projection
            Contract.RecipeNameEntry.COLUMN_RECIPE_ID,
            Contract.RecipeNameEntry.COLUMN_RECIPE_NAME,
            Contract.RecipeNameEntry.COLUMN_RECIPE_STEPS,
            Contract.RecipeNameEntry.COLUMN_RECIPE_INGREDIENTS,
    };

    public static final int COL_RECIPE_ID = 0;
    public static final int COL_RECIPE_NAME = 1;
    public static final int COL_RECIPE_STEPS= 2;
    public static final int COL_RECIPE_INGREDIENTS = 3;

    private static final int RECIPES_LOADER = 0;
    private NameAdapter mAdapter;
    private RecyclerView mRecylerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private Context context = getActivity();
    private JSONArray recipeDetailsArray;
    private JSONArray recipeIngredientsArray;
    private String stepId;
    private String stepShortDescription;
    private String stepDescription;
    private String stepVideoURL;
    private String stepThumbnailURL;
    private String ingredientName;
    private String ingredientQuanity;
    private String ingredientMeasure;
    private String mainRecipeIndex;


    public NameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_main, container, false);
        mRecylerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recipe_names);
        View emptyView = rootView.findViewById(R.id.recyclerview_recipe_empty);
        int numberOfColumns = getActivity().getResources().getInteger(R.integer.recyclerview_column);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        //View emptyView = rootView.findViewById(R.id.recyclerview_forecast_empty);
        // The ForecastAdapter will take data from a source and
        // use it to populate the RecyclerView it's attached to.
        mAdapter = new NameAdapter(context, this, emptyView);
        mRecylerView.setAdapter(mAdapter);



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle){
        switch (loaderId){
            case RECIPES_LOADER:
                Uri recipeNameQuery = Contract.RecipeNameEntry.CONTENT_URI;
                return new CursorLoader(getActivity(),
                        recipeNameQuery,
                        RECIPE_COLUMNS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw  new RuntimeException("Loader not Implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mAdapter.swapCursor(data);
        updateEmptyView();
        if(mPosition == RecyclerView.NO_POSITION){
            mPosition = 0;
        }
        mRecylerView.smoothScrollToPosition(mPosition);

        //setMainRecipeIndex(data.getString(COL_RECIPE_ID));
    }

    private void updateEmptyView(){
        if(mAdapter.getItemCount() == 0){
            TextView textViewEmpty = (TextView) getView().findViewById(R.id.recyclerview_recipe_empty);
            if(null != textViewEmpty){
                int message = R.string.empty_recipe_list;
                @SyncAdapter.RecipeServerStatus int recipeStatus = Utility.getRecipeServerStaus(getActivity());
                switch(recipeStatus){
                    case SyncAdapter.RECIPE_STATUS_SERVER_DOWN:
                        message = R.string.empty_recipe_list_server_down;
                        break;
                    case SyncAdapter.RECIPE_STATUS_SERVER_INVALID:
                        message = R.string.empty_recipe_list_server_error;
                        break;
                    default:
                        if(!Utility.isNetworkAvailable(getActivity())){
                            message = R.string.empty_recipe_list_no_network;
                        }

                }
                textViewEmpty.setText(message);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(Bundle bundleStepsandIngredients){

        //Utility.fillRecipeStepsTable(getContext(),bundleStepsandIngredients );
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtras(bundleStepsandIngredients);
        startActivity(detailIntent);
    }







}

