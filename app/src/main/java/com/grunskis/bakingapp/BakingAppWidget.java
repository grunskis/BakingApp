package com.grunskis.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.widget.RemoteViews;

import com.grunskis.bakingapp.utilities.UIUtilities;

public class BakingAppWidget extends AppWidgetProvider {

    private static final String PREFS_NAME = "com.grunskis.bakingapp.BakingAppWidget";
    private static final String PREFS_KEY_PREFIX = "ingredients_";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String ingredientString) {

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.baking_app_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        SpannableString ingredients = UIUtilities.formatIngredientString(ingredientString);
        views.setTextViewText(R.id.appwidget_text, ingredients);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            String ingredients = loadWidgetData(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredients);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            deleteWidgetData(context, appWidgetId);
        }
    }

    public static void saveWidgetData(Context context, int appWidgetId, String ingredients) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREFS_KEY_PREFIX + appWidgetId, ingredients);
        prefs.apply();
    }

    public static String loadWidgetData(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREFS_KEY_PREFIX + appWidgetId, "");
    }

    static void deleteWidgetData(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREFS_KEY_PREFIX + appWidgetId);
        prefs.apply();
    }
}
