package com.example.tanse.baking.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.tanse.baking.R;

import static com.example.tanse.baking.sync.SyncAdapter.ACTION_DATA_UPDATED;

/**
 * Created by Tanseer on 6/11/2017.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RecipeNameWidgetProvider extends AppWidgetProvider{

    public static final String TOAST_ACTION = "com.example.tanse.baking.TOAST_ACTION";
    public static final String RECIPE_ITEM = "com.example.tanse.baking.RECIPE_ITEM";
    public static final String RECIPE_NAME = "com.example.tanse.baking.RECIPE_NAME";
    public static final String ACTION_RECIPE_CHANGED = "com.example.tanse.baking.ACTION_RECIPE_CHANGED";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for(int appWidgetId: appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_recipe_name);

            Intent intent = new Intent(context, RecipeNameWidgetRemoteViewsService.class);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                setRemoteAdapter(context, views, intent);
            }else{
                setRemoteAdapterV11(context,views, intent);
            }
            views.setEmptyView(R.id.widget_recipe_name_list, R.id.widget_recipe_name_empty);
            views.setTextViewText(R.id.widget_name_header, "Baking");

            Intent toastIntent = new Intent(context, RecipeNameWidgetProvider.class);
            toastIntent.setAction(RecipeNameWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_recipe_name_list, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent){
        super.onReceive(context, intent);
        if(ACTION_DATA_UPDATED.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int [] appWidgetsIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsIds, R.id.widget_recipe_name_list);

        }

        if (intent.getAction().equals(TOAST_ACTION)) {
            String recipeIndex = intent.getStringExtra(RECIPE_ITEM);
            String recipeName = intent.getStringExtra(RECIPE_NAME);
            Toast.makeText(context, recipeName, Toast.LENGTH_SHORT).show();
            setRecipeNameAndIndexForIngredientWidget(context, recipeIndex, recipeName);
            Intent dataUpdatedIntent = new Intent(ACTION_RECIPE_CHANGED)
                    .setPackage(context.getPackageName());
            context.sendBroadcast(dataUpdatedIntent);
        }


    }

    /**
     * sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views, Intent intent){
        views.setRemoteAdapter(R.id.widget_recipe_name_list,intent);
    }

    /**
     * sets the remote adapter used to fill in the list items
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, Intent intent){
        views.setRemoteAdapter(0,R.id.widget_recipe_name_list,intent);
    }

    static private void setRecipeNameAndIndexForIngredientWidget(Context context, String recipeId, String recipeName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(context.getString(R.string.pref_recipe_id_key), recipeId);
        spe.putString(context.getString(R.string.pref_recipe_name_key), recipeName);
        spe.apply();

    }

}
