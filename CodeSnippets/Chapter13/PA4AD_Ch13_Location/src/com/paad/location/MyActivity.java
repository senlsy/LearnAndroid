package com.paad.location;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


public class MyActivity extends Activity implements
        CompoundButton.OnCheckedChangeListener
{
    
    LocationListener myLocationListener;
    
    PendingIntent pendingIntent;
    
    BroadcastReceiver proximityIntentReceiver;
    
    private Switch s;
    
    private EditText la_edit, lo_edit;
    
    private TextView tv;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.findViewById(R.id.button1).setVisibility(View.GONE);
        this.findViewById(R.id.switch2).setVisibility(View.GONE);
        la_edit=(EditText)this.findViewById(R.id.la_edit);
        lo_edit=(EditText)this.findViewById(R.id.lo_edit);
        s=(Switch)findViewById(R.id.switch1);
        s.setOnCheckedChangeListener(this);
        tv=(TextView)this.findViewById(R.id.tv1);
        proximityIntentReceiver=new BroadcastReceiver(){
            
            @Override
            public void onReceive(Context context, Intent intent) {
                String key=LocationManager.KEY_PROXIMITY_ENTERING;
                Boolean entering=intent.getBooleanExtra(key, false);
                tv.append(" 进入区域范围内了 ?=" + entering + "\n");
            }
        };
    }
    
    private void updateSpf() {
        SharedPreferences spf=getPreferences(MODE_PRIVATE);
        la_edit.setText(spf.getString("lat", ""));
        lo_edit.setText(spf.getString("lng", ""));
        s.setChecked(spf.getBoolean("ischeck", false));
    }
    
    private void saveSpf() {
        SharedPreferences spf=getPreferences(MODE_PRIVATE);
        Editor edit=spf.edit();
        edit.putString("lat", la_edit.getText().toString());
        edit.putString("lng", lo_edit.getText().toString());
        edit.putBoolean("ischeck", s.isChecked());
        edit.apply();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateSpf();
        // String serviceString = Context.LOCATION_SERVICE;
        // LocationManager locationManager;
        // locationManager = (LocationManager) getSystemService(serviceString);
        // if (EMULATOR_TESTING) {
        //
        // locationManager.requestLocationUpdates(
        // LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
        //
        // public void onLocationChanged(Location location) {
        // }
        //
        // public void onProviderDisabled(String provider) {
        // }
        //
        // public void onProviderEnabled(String provider) {
        // }
        //
        // public void onStatusChanged(String provider,
        // int status, Bundle extras) {
        // }
        // });
        // }
        //
        // Criteria criteria = new Criteria();
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // criteria.setPowerRequirement(Criteria.POWER_LOW);
        // criteria.setAltitudeRequired(false);
        // criteria.setBearingRequired(false);
        // criteria.setSpeedRequired(false);
        // criteria.setCostAllowed(true);
        // registerLocationUpdates(locationManager);
        // registerPendingIntentLoctionListener(locationManager);
    }
    
    @Override
    protected void onPause() {
        // unregisterReceiver(proximityIntentReceiver);
        // String serviceString = Context.LOCATION_SERVICE;
        // LocationManager locationManager;
        // locationManager = (LocationManager) getSystemService(serviceString);
        // locationManager.removeUpdates(myLocationListener);
        // locationManager.removeUpdates(pendingIntent);
        // PendingIntent proximityIntent = PendingIntent.getBroadcast(this, -1,
        // new Intent(TREASURE_PROXIMITY_ALERT), 0);
        // locationManager.removeProximityAlert(proximityIntent);
        super.onPause();
        saveSpf();
        if(s.isChecked())
            s.setChecked(false);
    }
    
    // 监听类方式回调
    private void registerLocationUpdates(LocationManager locationManager) {
        /**
         * Listing 13-4: Requesting location updates Using a Location Listener
         */
        String provider=LocationManager.GPS_PROVIDER;
        int t=5000; // milliseconds
        int distance=5; // meters
        LocationListener myLocationListener=new LocationListener(){
            
            public void onLocationChanged(Location location) {
            }
            
            public void onProviderDisabled(String provider) {
            }
            
            public void onProviderEnabled(String provider) {
            }
            
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
        locationManager.requestLocationUpdates(provider, t, distance, myLocationListener);
        this.myLocationListener=myLocationListener;
    }
    
    // pendingIntent方式回调
    private void registerPendingIntentLoctionListener(LocationManager locationManager) {
        /**
         * Listing 13-5: Requesting location updates using a Pending Intent
         */
        String provider=LocationManager.GPS_PROVIDER;
        int t=5000; // milliseconds
        int distance=5; // meters
        final int locationUpdateRC=0;
        final String locationUpdateAction="com.paad.LOCATION_UPDATE_RECEIVED";
        int flags=PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent=new Intent(locationUpdateAction);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this, locationUpdateRC, intent, flags);
        locationManager.requestLocationUpdates(provider, t, distance, pendingIntent);
        // Register Receiver
        this.pendingIntent=pendingIntent;
        registerReceiver(new MyLocationUpdateReceiver(), new IntentFilter(locationUpdateAction));
    }
    
    // 进入某一范围通知-----------------------
    /**
     * Listing 13-8: Setting a proximity alert
     */
    private static final String TREASURE_PROXIMITY_ALERT="com.paad.treasurealert";
    
    private void setProximityAlert(boolean ischeck, double lat, double lng) {
        String locService=Context.LOCATION_SERVICE;
        LocationManager locationManager=(LocationManager)getSystemService(locService);
        if( !ischeck){
            unregisterReceiver(proximityIntentReceiver);// 取消监听
            PendingIntent proximityIntent=PendingIntent.getBroadcast(this, -1, new Intent(TREASURE_PROXIMITY_ALERT), 0);
            locationManager.removeProximityAlert(proximityIntent);
            return;
        }
        IntentFilter filter=new IntentFilter(TREASURE_PROXIMITY_ALERT);
        registerReceiver(proximityIntentReceiver, filter);// 注册监听
        float radius=100f; // meters 半径
        long expiration= -1; // do not expire 不过期
        Intent intent=new Intent(TREASURE_PROXIMITY_ALERT);
        PendingIntent proximityIntent=PendingIntent.getBroadcast(this, -1, intent, 0);
        locationManager.addProximityAlert(lat, lng, radius, expiration, proximityIntent);
    }
    
    // -------------------------
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        double lat=0;
        double lng=0;
        if(isChecked)
            try{
                Log.e("lintest", "---onCheckedChanged---" + isChecked);
                SharedPreferences spf=getPreferences(MODE_PRIVATE);
                lat=Double.parseDouble(spf.getString("lat", null));
                lng=Double.parseDouble(spf.getString("lng", null));
            } catch(Exception e){
                buttonView.setChecked(false);
                return;
            }
        else
            Log.e("lintest", "---onCheckedChanged---" + isChecked);
        setProximityAlert(isChecked, lat, lng);
    }
}