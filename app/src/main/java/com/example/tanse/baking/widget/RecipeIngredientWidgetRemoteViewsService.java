package com.example.tanse.baking.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.tanse.baking.R;
import com.example.tanse.baking.Utility.Utility;
import com.example.tanse.baking.data.Contract;

/**
 * Created by Supreme on 6/18/2017.
 */

public class RecipeIngredientWidgetRemoteViewsService extends RemoteViewsService {
    private  final String LOG_TAG = RecipeIngredientWidgetRemoteViewsService.class.getSimpleName();

    private static final int INDEX_INGREDIENT_RECIPE_ID = 1;
    private static final int INDEX_INGREDIENT_NAME = 2;
    private static final int INDEX_INGREDIENT_QUANTITY = 3;
    private static final int INDEX_INGREDIENT_MEASURE= 4;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new RemoteViewsFactory() {

            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();


                String recipeNameIndex = Utility.getRecipeIdForIngredientWidget(getApplicationContext());
                data = getContentResolver().query(Contract.RecipeIngredientsEntry.buildIngredientsUri(recipeNameIndex),
                        Contract.RecipeIngredientsEntry.RECIPE_INGREDIENTS_COLUMNS,
                        null, null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }


            @Override
            public void onDestroy() {
                if(data != null){
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0: data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)){
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_ingredient_list_item);

                String ingredientName = data.getString(INDEX_INGREDIENT_NAME);
                String ingredientQuanity = data.getString(INDEX_INGREDIENT_QUANTITY);
                String ingredientMeasure = data.getString(INDEX_INGREDIENT_MEASURE);


                views.setTextViewText(R.id.widget_recipe_ingredient_name, ingredientName);
                views.setTextViewText(R.id.widget_recipe_ingredient_quanity_measure, ingredientQuanity + " " + ingredientMeasure );

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_ingredient_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position)) {
                    return data.getLong(INDEX_INGREDIENT_RECIPE_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}

