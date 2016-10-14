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
        // �˴�����widget����Ҫ�ʺ�������widgetԪ������Ϣ�ĸ���Ƶ��
        // // ����ÿһ��widget
        for(int appWidgetId:appWidgetIds){
            // ����R.layout.myappwidget_layout���ֵ�RemoteView
            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
            // ���Զ�views������
            Intent intent=new Intent(context, MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);// ��RemoteView���ĸı���µ�widget
        }
    }
    
    public static String FORCE_WIDGET_UPDATE="com.paad.mywidget.FORCE_WIDGET_UPDATE";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(FORCE_WIDGET_UPDATE.equals(intent.getAction())){
            // �ü������ӵ�Intent���ﵽ����widget��Ч��
            AppWidgetManager appwm=AppWidgetManager.getInstance(context);
            ComponentName widget=new ComponentName(context, AppWidget.class);
            int[] ids=appwm.getAppWidgetIds(widget);
            if(ids == null)
                return;
            for(int widgetid:ids){
                // ����ÿһ��widget
                RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
                // ���Զ�views������
                Intent widgetIntent=new Intent(context, MainActivity.class);
                PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, widgetIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
                appwm.updateAppWidget(widgetid, views);// ��RemoteView���ĸı���µ�widget
            }
        }
    }
}