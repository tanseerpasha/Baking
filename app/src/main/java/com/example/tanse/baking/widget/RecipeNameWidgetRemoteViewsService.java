package com.example.tanse.baking.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.tanse.baking.R;
import com.example.tanse.baking.data.Contract;

/**
 * Created by Tanseer on 6/11/2017.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RecipeNameWidgetRemoteViewsService extends RemoteViewsService{

    private  final String LOG_TAG = RecipeNameWidgetRemoteViewsService.class.getSimpleName();

    private static final int INDEX_RECIPE_ID = 1;
    private static final int INDEX_RECIPE_NAME = 2;
//    private static final int INDEX_RECIPE_STEPS= 3;

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
                data = getContentResolver().query(Contract.RecipeNameEntry.CONTENT_URI,
                        Contract.RecipeNameEntry.RECIPE_NAME_COLUMNS,
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

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_name_list_item);
                String recipeName = data.getString(INDEX_RECIPE_NAME);
                String recipeMainIndex = data.getString(INDEX_RECIPE_ID);
//                String recipeSteps = data.getString(INDEX_RECIPE_STEPS);

//                Bundle bundle = new Bundle();
//                bundle.putString("RECIPE_MAIN_INDEX", recipeMainIndex);
//                bundle.putString("RECIPE_MAIN_NAME", recipeName);
//                bundle.putString("RECIPE_STEPS", recipeSteps);
                views.setTextViewText(R.id.widget_recipe_name, recipeName);

                Bundle extras = new Bundle();
                extras.putString(RecipeNameWidgetProvider.RECIPE_ITEM, recipeMainIndex);
                extras.putString(RecipeNameWidgetProvider.RECIPE_NAME, recipeName);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.widget_recipe_name, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_name_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position)) {
                    return data.getLong(INDEX_RECIPE_ID);
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
