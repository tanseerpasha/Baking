package com.example.tanse.baking.recipeingredients;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tanse.baking.R;
import com.example.tanse.baking.data.Contract;

/**
 * Created by Tanseer on 6/5/2017.
 */

public class IngredientFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = IngredientFragment.class.getSimpleName();

    // For the ingredient view we're showing ingredients of the selected recipe from the stored data.
    // Specify the columns we need
    private static final String[] RECIPEINGREDIENTS_COLUMNS_PROJECTION = {    //projection
            Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID,
            Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_NAME,
            Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_QUANTITY,
            Contract.RecipeIngredientsEntry.COLUMN_INGREDIENT_MEASURE,
    };
    public static final int COL_INGREDIENT_RECIPEID = 0;
    public static final int COL_INGREDIENT_NAME = 1;
    public static final int COL_INGREDIENT_QUANTITY = 2;
    public static final int COL_INGREDIENT_MEASURE = 3;


    private static final int RECIPE_INGREDIENTS_LOADER = 0;
    private IngredientAdapter mAdapter;
    private RecyclerView mRecylerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private Context context = getActivity();
    int lastFirstVisiblePosition=0;
    private String recipeSelected;
    private Uri recipeIngredientsUri;

    public IngredientFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments !=null){
            recipeSelected = arguments.getString("RECIPE_MAIN_INDEX");
            recipeIngredientsUri = Contract.RecipeIngredientsEntry.buildIngredientsUri(recipeSelected);
        }



        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        mRecylerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recipe_ingredients);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new IngredientAdapter(context);
        mRecylerView.setAdapter(mAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            ((LinearLayoutManager) mRecylerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
            lastFirstVisiblePosition = 0;
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_INGREDIENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle){

        switch (loaderId){
            case RECIPE_INGREDIENTS_LOADER:
//                Log.d("TANSEERINGRED_URI1","" + new CursorLoader(getActivity(),
//                        recipeIngredientsUri,
//                        RECIPEINGREDIENTS_COLUMNS_PROJECTION,
//                        null,
//                        null,
//                        null) );
                return new CursorLoader(getActivity(),
                        recipeIngredientsUri,
                        RECIPEINGREDIENTS_COLUMNS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw  new RuntimeException("Loader not Implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data){
        mAdapter.swapCursor(data);
        if(mPosition == RecyclerView.NO_POSITION){
            mPosition = 0;
        }
        mRecylerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mAdapter.swapCursor(null);
    }


}
