package com.paad.earthquake.widget;

import com.paad.earthquake.Earthquake;
import com.paad.earthquake.R;
import com.paad.earthquake.R.id;
import com.paad.earthquake.R.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


public class EarthquakeListWidget extends AppWidgetProvider
{
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // Iterate over the array of active widgets.
        final int N=appWidgetIds.length;
        for(int i=0;i < N;i++){
            int appWidgetId=appWidgetIds[i];
            // 将collection widget与RemoteViewsService绑定
            Intent intent=new Intent(context, EarthquakeRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.quake_collection_widget);
            views.setRemoteAdapter(R.id.widget_list_view, intent);
            //
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text);
            // 设置交互性的PendingIntent模板
            Intent templateIntent=new Intent(context, Earthquake.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent=PendingIntent.getActivity(
                                                                          context, 0, templateIntent,
                                                                          PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list_view,
                                           templatePendingIntent);
            // 更新collection widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    
    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
                                        newOptions);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
    
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
