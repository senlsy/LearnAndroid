package com.paad.widget4provider;

import com.paad.PA4AD_Ch14_MyWidget.R;
import com.paad.stackwidget.StackRemoteViewsService;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class MediaStackWidget extends AppWidgetProvider
{
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N=appWidgetIds.length;
        for(int i=0;i < N;i++){
            int appWidgetId=appWidgetIds[i];
            // ����mediawidget_layout���ֵ�RemoteView
            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.mediawidget_layout);
            // �����widgetʵ���Ĳ��ְ󶨵�MediaRemoteViewsService��
            Intent intent=new Intent(context, MediaRemoteViewsService.class);
            // ��ô���һ��appWidgetId���ȱ������MediaRemoteViewsService��Ϊ�����ò�ͬ��RemoteViewFactoty
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(appWidgetId, R.id.mediastack_view, intent);
            // ���õ�����Ϊ�յ�ʱ����ʾR.id.mediastack_empty
            views.setEmptyView(R.id.mediastack_view, R.id.mediastack_empty);
            // ------------------------------------------------------------
            // ����Ч��ԭ���޷�Ϊÿһ��item����һ��PendingIntent��
            // ����ֻҪΪ��������widget����һ��Intentģ�壬Ȼ��������󶨵�RemoteViewsFactory��
            // ��getViewAt()���ÿ��Itemʱ��������Intentģ�壬���û����Item��ʱ��ᴥ�����PendingIntent
            Intent templateIntent=new Intent(Intent.ACTION_VIEW);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent=PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.mediastack_view, templatePendingIntent);
            // --------------------
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}