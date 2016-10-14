package com.paad.datatransfer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


public class MyActivity extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String service=Context.CONNECTIVITY_SERVICE;
        final ConnectivityManager connectivity=(ConnectivityManager)getSystemService(service);
        registerReceiver(new BroadcastReceiver(){
            
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean backgroundEnabled=connectivity.getBackgroundDataSetting();
                setBackgroundData(backgroundEnabled);
            }
        }, new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED));
        NetworkInfo activeNetwork=connectivity.getActiveNetworkInfo();
        boolean isConnected=((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
        boolean isWiFi=activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }
    
    private void setBackgroundData(boolean backgroundEnabled) {
    }
}