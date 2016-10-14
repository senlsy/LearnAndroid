package com.paad.simplewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class SimpleWidget extends AppWidgetProvider
{
    
    
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e("lintest", "SimpleWidget  onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("lintest", "SimpleWidget  onReceive");
    }
    
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.e("lintest", "SimpleWidget  onAppWidgetOptionsChanged");
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.e("lintest", "SimpleWidget  onDeleted");
    }
    
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e("lintest", "SimpleWidget  onEnabled");
    }
    
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.e("lintest", "SimpleWidget  onDisabled");
    }
}