package com.example.tanse.baking.recipenames;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tanse.baking.R;
//import com.example.tanse.baking.recipedetails.DetailActivity;

/**
 * Created by tanse on 5/15/2017.
 */

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameAdapterViewHolder> {

    final private Context mContext;
    final private NameAdapterOnClickLHandler mClickHandler;
    final private View mEmptyView;
    private Cursor mCursor;

    public interface NameAdapterOnClickLHandler{
        void onClick(Bundle recipeStepsAndIngredients);
    }

    public NameAdapter(@NonNull Context context, NameAdapterOnClickLHandler clickLHandler, View emptyView){
        mContext = context;
        mClickHandler = clickLHandler;
        mEmptyView = emptyView;
    }

    @Override
    public NameAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType){
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_name_cardview, viewGroup, false);
        view.setFocusable(true);
        return new NameAdapterViewHolder(view);
    }

    public void onBindViewHolder(NameAdapterViewHolder nameAdapterViewHolder, int position){
        mCursor.moveToPosition(position);
        String recipeNameString = mCursor.getString(NameFragment.COL_RECIPE_NAME);
        nameAdapterViewHolder.mRecipeName.setText(recipeNameString);
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
        mEmptyView.setVisibility(getItemCount() ==0 ? View.VISIBLE : View.GONE);
    }

    public class NameAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mRecipeName;
        public final CardView mCV;

        public NameAdapterViewHolder(View view){
            super(view);
            mRecipeName = (TextView) view.findViewById(R.id.widget_recipe_name);
            mCV = (CardView) view.findViewById(R.id.cardview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            //String recipeIndex = mCursor.getString(Contract.RecipeNameEntry.COLUMN_RECIPE_STEPS);
            String recipeSteps = mCursor.getString(NameFragment.COL_RECIPE_STEPS);
            String recipeMainIndex = mCursor.getString(NameFragment.COL_RECIPE_ID);
            String recipeMainName = mCursor.getString(NameFragment.COL_RECIPE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString("RECIPE_STEPS", recipeSteps);
            bundle.putString("RECIPE_MAIN_INDEX", recipeMainIndex);
            bundle.putString("RECIPE_MAIN_NAME", recipeMainName);
            //String recipeIndex = mCursor.getString(NameFragment.COL_RECIPE_ID);
            mClickHandler.onClick(bundle);
        }
    }


}