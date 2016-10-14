/**
 * Listing 13-7: Design pattern for switching Location Providers when a better alternative becomes available
 */
package com.paad.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


public class DynamicProvidersActivity extends Activity implements
        CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
    
    private LocationManager locationManager;
    
    private final Criteria criteria=new Criteria();
    
    private static final String TAG="DYNAMIC_LOCATION_PROVIDER";
    
    private TextView textView;
    
    private LinearLayout ll;
    
    private Switch s;
    
    private Button locationBtn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ll=(LinearLayout)this.findViewById(R.id.ll);
        ll.setVisibility(View.GONE);
        s=(Switch)this.findViewById(R.id.switch2);
        s.setOnCheckedChangeListener(this);
        textView=(TextView)findViewById(R.id.tv1);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        locationBtn=(Button)this.findViewById(R.id.button1);
        locationBtn.setOnClickListener(this);
        // Get a reference to the Location Manager
        String svcName=Context.LOCATION_SERVICE;
        locationManager=(LocationManager)getSystemService(svcName);
        // Specify Location Provider criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        // Only for Android 3.0 and above
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        // End of Android 3.0 and above only
    }
    
    @Override
    protected void onPause() {
        unregisterAllListeners();
        super.onPause();
        Editor edit=getPreferences(MODE_PRIVATE).edit();
        edit.putBoolean("ischeck", s.isChecked());
        edit.apply();
        s.setChecked(false);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        boolean ischeck=getPreferences(MODE_PRIVATE).getBoolean("ischeck", false);
        s.setChecked(ischeck);
    }
    
    private void registerListener() {
        unregisterAllListeners();
        String bestProvider=locationManager.getBestProvider(criteria, false);// �������û���δ�����е��ṩ����
        String bestAvailableProvider=locationManager.getBestProvider(criteria, true);// �����õ��ṩ������
        textView.append("��ö�λ��ʽ=" + bestProvider + " / ��ÿ��ö�λ��ʽ=" + bestAvailableProvider + "\n");
        if(bestProvider == null)
            textView.setText("���豸û��GPS����");
        else if(bestAvailableProvider.equals(bestProvider))
            locationManager.requestLocationUpdates(bestAvailableProvider, 5000, 5, bestAvailableProviderListener);
        else{
            locationManager.requestLocationUpdates(bestProvider, 5000, 5, bestProviderListener);
            if(bestAvailableProvider != null)
                locationManager.requestLocationUpdates(bestAvailableProvider, 5000, 5, bestAvailableProviderListener);
            else{
                List<String> allProviders=locationManager.getAllProviders();
                for(String provider:allProviders)
                    locationManager.requestLocationUpdates(provider, 0, 0, bestProviderListener);
            }
        }
        locationManager.addGpsStatusListener(statusListener);// �����仯�ص�
    }
    
    // ����״̬������------------------------
    private final GpsStatus.Listener statusListener=new GpsStatus.Listener(){
        
        public void onGpsStatusChanged(int event) { // GPS״̬�仯ʱ�Ļص�����������
            GpsStatus status=locationManager.getGpsStatus(null); // ȡ��ǰ״̬
            if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
                Iterator<GpsSatellite> it=status.getSatellites().iterator();
                numSatelliteList.clear();
                while(it.hasNext()){
                    GpsSatellite s=it.next();
                    numSatelliteList.add(s);
                }
            }
        }
    };
    
    private List<GpsSatellite> numSatelliteList=new ArrayList<GpsSatellite>(); // �����ź�
    
    // ȡ�����м���-----------------------------
    private void unregisterAllListeners() {
        locationManager.removeGpsStatusListener(statusListener);
        locationManager.removeUpdates(bestProviderListener);
        locationManager.removeUpdates(bestAvailableProviderListener);
    }
    
    private void reactToLocationChange(Location location) {
        if(location != null){
            Time t=new Time();
            t.setToNow();
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            double altitude=location.getAltitude(); // ����
            textView.setText("�������Ǹ�����" + numSatelliteList.size() + "\nγ�ȣ�"
                             + latitude + "\n���ȣ�" + longitude + "\n���Σ�" + altitude
                             + "\nʱ�䣺" + t.year + "��" + (t.month + 1) + "��" + t.monthDay + "��" + t.hour
                             + ":" + t.minute + ":" + t.second);
        }
        else{
            textView.setText("�޷���ȡ������Ϣ");
        }
    }
    
    // �ṩ����λ����-------------------------------------
    private LocationListener bestProviderListener=new LocationListener(){
        
        public void onLocationChanged(Location location) {
            reactToLocationChange(location);
        }
        
        public void onProviderDisabled(String provider) {
        }
        
        public void onProviderEnabled(String provider) {
            registerListener();
        }
        
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    
    private LocationListener bestAvailableProviderListener=new LocationListener(){
        
        public void onProviderEnabled(String provider) {
        }
        
        public void onProviderDisabled(String provider) {
            registerListener();
        }
        
        public void onLocationChanged(Location location) {
            reactToLocationChange(location);
        }
        
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    
    // --------------------------------------
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
            registerListener();
        else
            unregisterAllListeners();
    }
    
    // ���ζ�λ������
    private LocationListener singleListener=new LocationListener(){
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            changeBtn();
        }
        
        @Override
        public void onProviderEnabled(String provider) {
            changeBtn();
        }
        
        @Override
        public void onProviderDisabled(String provider) {
            changeBtn();
        }
        
        @Override
        public void onLocationChanged(Location location) {
            textView.append("la=" + location.getLatitude() + "\nlo=" + location.getLongitude() + "\n");
            changeBtn();
        }
        
        private void changeBtn() {
            locationBtn.setEnabled(true);
            locationManager.removeUpdates(singleListener);
        }
    };
    
    @Override
    public void onClick(View v) {
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, singleListener, null);
        locationBtn.setEnabled(false);
    }
}
