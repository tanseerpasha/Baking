package com.example.tanse.baking.recipedetails;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tanse.baking.R;


/**
 * Created by tanse on 5/15/2017.
 */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsAdapterViewHolder> {

    final private Context mContext;
    private Cursor mCursor;


    public DetailsAdapter(@NonNull Context context){
        this.mContext = context;

    }

    @Override
    public DetailsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType){
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_detail_textview, viewGroup, false);
        view.setFocusable(true);
        return new DetailsAdapterViewHolder(view);
    }

    public void onBindViewHolder(DetailsAdapterViewHolder detailsAdapterViewHolder, int position){
        mCursor.moveToPosition(position);
        final String recipeDetails = mCursor.getString(DetailFragment.COL_STEP_SHORTDESCRIPTION);
        String recipeStepId = mCursor.getString(DetailFragment.COL_STEP_ID);
        if(position == 0) {
            detailsAdapterViewHolder.mRecipeDetail.setText( recipeDetails);
        }else{
            detailsAdapterViewHolder.mRecipeDetail.setText(recipeStepId + ". " + recipeDetails);
        }
        final String recipeStepsId =  mCursor.getString(DetailFragment.COL_STEP_ID);
        final Bundle recipeStepSelected = new Bundle();
        recipeStepSelected.putString("RECIPE_STEP_ID",recipeStepsId );
        recipeStepSelected.putString("RECIPE_SHORT_DESCRIPTION",recipeDetails);

        detailsAdapterViewHolder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                callFragment(recipeStepSelected);
                //Toast.makeText(mContext, "You clicked " + recipeDetails + ", which is at cell position " + recipeStepsId, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void callFragment(Bundle bundle ) {
        if (mContext == null) {
            return;
        }
        if (mContext instanceof DetailActivity) {
            DetailActivity mDetailActivity = (DetailActivity) mContext;
            mDetailActivity.switchContent(bundle);
        }


    }

    @Override
    public int getItemCount(){
        if(null==mCursor) {
            return 0;
        }
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class DetailsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mRecipeDetail;
        public final View mView;


        public DetailsAdapterViewHolder(View view){
            super(view);
            mView = view;
            mRecipeDetail = (TextView) view.findViewById(R.id.recipe_detail_info);

        }
    }
}