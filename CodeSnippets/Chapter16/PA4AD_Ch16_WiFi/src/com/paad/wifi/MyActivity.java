package com.paad.wifi;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MyActivity extends Activity
{
    
    private static final String TAG="TAG";
    WifiManager wifi;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String service=Context.WIFI_SERVICE;
        wifi=(WifiManager)getSystemService(service);
        if( !wifi.isWifiEnabled())
            if(wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifi.setWifiEnabled(true);
        // ��ǰwifi����״̬-----------------
        WifiInfo info=wifi.getConnectionInfo();
        if(info.getBSSID() != null){
            // ת��Ϊ��ָ����Χ���źŵȼ�
            int strength=WifiManager.calculateSignalLevel(info.getRssi(), 5);
            // ��·�ٶ�
            int speed=info.getLinkSpeed();
            // ��·�ٶȵ�λ
            String units=WifiInfo.LINK_SPEED_UNITS;
            // ssid
            String ssid=info.getSSID();
            String cSummary=String.format("Connected to %s at %s%s. " + "Strength %s/5", ssid, speed, units, strength);
            Log.e("lintest", cSummary);
        }
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();// ɨ������ɽ����
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
    }
    
    BroadcastReceiver wifiScanReceiver=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results=wifi.getScanResults();
            ScanResult bestSignal=null;
            for(ScanResult result:results){
                if(bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
                    bestSignal=result;
            }
            String connSummary=String.format("%s networks found. %s is" + "the strongest.", results.size(), bestSignal.SSID);
            Toast.makeText(MyActivity.this, connSummary, Toast.LENGTH_LONG).show();
        }
    };
}