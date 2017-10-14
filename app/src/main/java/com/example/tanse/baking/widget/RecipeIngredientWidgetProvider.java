package com.example.tanse.baking.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.tanse.baking.R;
import com.example.tanse.baking.Utility.Utility;
import com.example.tanse.baking.sync.SyncAdapter;

import static com.example.tanse.baking.widget.RecipeNameWidgetProvider.ACTION_RECIPE_CHANGED;


/**
 * Created by Supreme on 6/18/2017.
 */

public class RecipeIngredientWidgetProvider extends AppWidgetProvider {

    public static final String INGREDIENT_CHANGE_ITEM = "com.example.tanse.baking.CHANGE_ITEM";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int appWidgetId: appWidgetIds){

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_ingredient);
            Intent intent = new Intent(context, RecipeIngredientWidgetRemoteViewsService.class);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                setRemoteAdapter(context, views, intent);
            }else{
                setRemoteAdapterV11(context,views, intent);
            }
            views.setEmptyView(R.id.widget_recipe_ingredient_list, R.id.widget_recipe_ingredient_empty);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (SyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetsIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsIds, R.id.widget_recipe_ingredient_list);
        }

        if (intent.getAction().equals(ACTION_RECIPE_CHANGED)) {
            String recipeName = Utility.getRecipeNameForIngredientWidget(context);

            RemoteViews rempoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_ingredient);
            ComponentName thisWidgetProvider = new ComponentName( context, RecipeIngredientWidgetProvider.class );
            rempoteViews.setTextViewText(R.id.widget_ingredient_header,recipeName );
            AppWidgetManager.getInstance(context).updateAppWidget( thisWidgetProvider, rempoteViews );

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), RecipeIngredientWidgetProvider.class);
            int [] appWidgetsIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsIds, R.id.widget_recipe_ingredient_list);

        }





    }



    /**
     * sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views, Intent intent){
        views.setRemoteAdapter(R.id.widget_recipe_ingredient_list,intent);
    }

    /**
     * sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, Intent intent){
        views.setRemoteAdapter(0,R.id.widget_recipe_ingredient_list,intent);
    }

}

