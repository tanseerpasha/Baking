package com.example.tanse.baking.recipedetails;

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
 * Created by Tanseer on 6/4/2017.
 */

public class DetailFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>

{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";


    // For the detail view we're showing only the shortdescription from the stored data.
    // Specify the columns we need
    private static final String[] RECIPEDETAILS_COLUMNS_PROJECTION = {    //projection
            Contract.RecipeDetailsEntry.COLUMN_STEP_ID,
            Contract.RecipeDetailsEntry.COLUMN_STEP_SHORTDESCRIPTION,
    };
    public static final int COL_STEP_ID = 0;
    public static final int COL_STEP_SHORTDESCRIPTION = 1;


    private static final int RECIPE_DETAILS_LOADER = 0;
    private DetailsAdapter mAdapter;
    private RecyclerView mRecylerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private Context context = getActivity();
    int lastFirstVisiblePosition=0;


    public DetailFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lastFirstVisiblePosition = ((LinearLayoutManager)mRecylerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(SELECTED_KEY, lastFirstVisiblePosition);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        mRecylerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recipe_details);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DetailsAdapter(getContext());
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
        getLoaderManager().initLoader(RECIPE_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle){
        switch (loaderId){
            case RECIPE_DETAILS_LOADER:
                Uri recipeDetailsQuery = Contract.RecipeDetailsEntry.CONTENT_URI;
                return new CursorLoader(getActivity(),
                        recipeDetailsQuery,
                        RECIPEDETAILS_COLUMNS_PROJECTION,
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
