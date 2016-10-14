package com.paad.PA4AD_Ch14_MyWidget;

import com.paad.simplewidget.AppWidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button refresh=(Button)findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new OnClickListener(){
            
            public void onClick(View view) {
                forceRefresh();
            }
        });
        Button alarmButton=(Button)findViewById(R.id.buttonAlarm);
        alarmButton.setOnClickListener(new OnClickListener(){
            
            public void onClick(View view) {
                setRefreshAlarm(MainActivity.this);
            }
        });
    }
    
    private void forceRefresh() {
        sendBroadcast(new Intent(AppWidget.FORCE_WIDGET_UPDATE));
    }
    
    private void setRefreshAlarm(Context context) {
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=PendingIntent.getBroadcast(context, 0, new Intent(AppWidget.FORCE_WIDGET_UPDATE), 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, pi);
    }
}