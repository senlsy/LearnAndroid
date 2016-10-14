package com.paad.stackwidget;

import com.paad.PA4AD_Ch14_MyWidget.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class StackWidget extends AppWidgetProvider
{
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N=appWidgetIds.length;
        for(int i=0;i < N;i++){
            int appWidgetId=appWidgetIds[i];
            // 创建stackwidget_layout布局的RemoteView
            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.stackwidget_layout);
            // 将这个widget实例的布局绑定到MyRemoteViewsService上
            Intent intent=new Intent(context, StackRemoteViewsService.class);
            // 最好传递一个appWidgetId，比便可以在MyRemoteViewsService里为它配置不同的RemoteViewFactoty
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(appWidgetId, R.id.stackwidget_view, intent);
            // 设置当集合为空的时候显示R.id.stackwidget_empty
            views.setEmptyView(R.id.stackwidget_view, R.id.stackwidget_empty);
            // ------------------------------------------------------------
            // 出于效率原因，无法为每一条item分配一个PendingIntent。
            // 所以只要为整个集合widget分配一个Intent模板，然后在与其绑定的RemoteViewsFactory内
            // 的getViewAt()填充每条Item时，填充这个Intent模板，当用户点击Item的时候会触发这个PendingIntent
            Intent templateIntent=new Intent(Intent.ACTION_VIEW);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent templatePendingIntent=PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.stackwidget_view, templatePendingIntent);
            // --------------------
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}