package com.paad.simplewidget;

import com.paad.PA4AD_Ch14_MyWidget.R;
import com.paad.PA4AD_Ch14_MyWidget.R.id;
import com.paad.PA4AD_Ch14_MyWidget.R.layout;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;


public class WidgetConfiguration extends Activity implements OnClickListener
{
    
    private int appWidgetId=AppWidgetManager.INVALID_APPWIDGET_ID;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgetconfig);
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        if(extras != null){
            appWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        this.findViewById(R.id.buttonComplete).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonComplete)
        {
            AppWidgetManager appwm=AppWidgetManager.getInstance(this);
            RemoteViews views=new RemoteViews(this.getPackageName(), R.layout.simplewidget_layout);
            views.setTextViewText(R.id.widget_text, "查看widget的回调函数,配置Activity完成配置");
            appwm.updateAppWidget(appWidgetId, views);// 将RemoteView做的改变更新到widget
            Intent result=new Intent();
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, result);
            finish();
        }
        else if(v.getId() == R.id.buttonCancle)
        {
            setResult(RESULT_CANCELED, null);
            finish();
        }
    }
}