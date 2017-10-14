package com.example.tanse.baking.recipeingredients;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tanse.baking.R;

/**
 * Created by Tanseer on 6/5/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientAdapterViewHolder> {
    final private Context mContext;
    private Cursor mCursor;

    public IngredientAdapter(@NonNull Context context){
        mContext = context;
    }

    @Override
    public IngredientAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType){
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_ingredient_textview, viewGroup, false);
        view.setFocusable(true);
        return new IngredientAdapterViewHolder(view);
    }

    public void onBindViewHolder(IngredientAdapterViewHolder ingredientAdapterViewHolder, int position){
        mCursor.moveToPosition(position);

        String recipeIngredientName = mCursor.getString(IngredientFragment.COL_INGREDIENT_NAME);
        String recipeIngredientQuantity = mCursor.getString(IngredientFragment.COL_INGREDIENT_QUANTITY);
        String recipeIngredientMeasure = mCursor.getString(IngredientFragment.COL_INGREDIENT_MEASURE);

        ingredientAdapterViewHolder.mIngredientName.setText(recipeIngredientName);
        ingredientAdapterViewHolder.mIngredientQuanityMeasure.setText(
                recipeIngredientQuantity + "  " + recipeIngredientMeasure );

        if(position == 0) {
            ingredientAdapterViewHolder.mIngredientName.setBackgroundResource(R.color.grey);
            ingredientAdapterViewHolder.mIngredientQuanityMeasure.setBackgroundResource(R.color.grey);

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

    public class IngredientAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIngredientName;
        public final TextView mIngredientQuanityMeasure;


        public IngredientAdapterViewHolder(View view){
            super(view);
            mIngredientName = (TextView) view.findViewById(R.id.ingredient_name);
            mIngredientQuanityMeasure = (TextView) view.findViewById(R.id.ingrdient_quantitymeasure);

            int adapterPosition = getAdapterPosition();
            if (adapterPosition == 0){
                mIngredientName.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            }
        }
    }
}
