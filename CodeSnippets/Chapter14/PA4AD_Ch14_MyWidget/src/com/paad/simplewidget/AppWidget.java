package com.paad.simplewidget;

import com.paad.PA4AD_Ch14_MyWidget.MainActivity;
import com.paad.PA4AD_Ch14_MyWidget.R;
import com.paad.PA4AD_Ch14_MyWidget.R.id;
import com.paad.PA4AD_Ch14_MyWidget.R.layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.widget.RemoteViews;


public class AppWidget extends AppWidgetProvider
{
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 此处更新widget，主要适合于描述widget元数据信息的更新频率
        // // 更新每一个widget
        for(int appWidgetId:appWidgetIds){
            // 创建R.layout.myappwidget_layout布局的RemoteView
            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
            // 可以对views做设置
            Intent intent=new Intent(context, MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);// 将RemoteView做的改变更新到widget
        }
    }
    
    public static String FORCE_WIDGET_UPDATE="com.paad.mywidget.FORCE_WIDGET_UPDATE";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(FORCE_WIDGET_UPDATE.equals(intent.getAction())){
            // 用监听附加的Intent来达到更新widget的效果
            AppWidgetManager appwm=AppWidgetManager.getInstance(context);
            ComponentName widget=new ComponentName(context, AppWidget.class);
            int[] ids=appwm.getAppWidgetIds(widget);
            if(ids == null)
                return;
            for(int widgetid:ids){
                // 更新每一个widget
                RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
                // 可以对views做设置
                Intent widgetIntent=new Intent(context, MainActivity.class);
                PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, widgetIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
                appwm.updateAppWidget(widgetid, views);// 将RemoteView做的改变更新到widget
            }
        }
    }
}