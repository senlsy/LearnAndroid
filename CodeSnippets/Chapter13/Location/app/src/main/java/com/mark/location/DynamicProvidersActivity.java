/**
 * Listing 13-7: Design pattern for switching Location Providers when a better alternative becomes available
 */
package com.mark.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mark.quick.base_library.utils.android.permission.PermissionsEnum;
import com.mark.quick.base_library.utils.android.permission.PermissionsUtils2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint("MissingPermission")
public class DynamicProvidersActivity extends Activity {

    PermissionsEnum[] REQ_PERMISSIONS = new PermissionsEnum[]{PermissionsEnum.ACCESS_FINE_LOCATION, PermissionsEnum.ACCESS_COARSE_LOCATION};
    private static final String TAG = "DYNAMIC_LOCATION_PROVIDER";
    private TextView mTvDisplay;
    private LinearLayout mLayoutNotify;
    private Switch mSwitchLocation;
    private Button mBtnLocation;


    private LocationManager mLocationManager;
    private final Criteria mCriteria = new Criteria();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayoutNotify = this.findViewById(R.id.layout_notify);
        mLayoutNotify.setVisibility(View.GONE);

        mTvDisplay = findViewById(R.id.tv_display);
        mTvDisplay.setMovementMethod(ScrollingMovementMethod.getInstance());

        mSwitchLocation = this.findViewById(R.id.switch_location);
        mSwitchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    register();
                } else {
                    unregister();
                }
            }
        });


        mBtnLocation = this.findViewById(R.id.btn_location);
        mBtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LocationManager.GPS_PROVIDER
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, singleListener, Looper.getMainLooper());
                mBtnLocation.setText("location....");
            }
        });


        // Get a reference to the Location Manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Specify Location Provider criteria
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        mCriteria.setAltitudeRequired(false);
        mCriteria.setBearingRequired(false);
        mCriteria.setSpeedRequired(false);
        mCriteria.setCostAllowed(true);
        // Only for Android 3.0 and above
        mCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        mCriteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
        mCriteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        mCriteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        // End of Android 3.0 and above only
        if (PermissionsUtils2.lackPermission(REQ_PERMISSIONS)) {
            PermissionsUtils2.requestPermissions(1, this, REQ_PERMISSIONS);
        }
    }

    @Override
    protected void onPause() {
        unregister();
        super.onPause();
        getPreferences(MODE_PRIVATE).edit().putBoolean("ischeck", mSwitchLocation.isChecked()).apply();
        mSwitchLocation.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwitchLocation.setChecked(getPreferences(MODE_PRIVATE).getBoolean("ischeck", false));
    }

    private void register() {

        unregister();

        String bestProvider = mLocationManager.getBestProvider(mCriteria, false);// 返回启用或者未启用中的提供中找
        String availableProvider = mLocationManager.getBestProvider(mCriteria, true);// 已启用的提供器中找
        mTvDisplay.append("best location way[" + bestProvider + "],best available way[" + availableProvider + "]\n");

        if (bestProvider == null) {
            mTvDisplay.setText("loss location function");
        } else if (availableProvider.equals(bestProvider)) {
            mLocationManager.requestLocationUpdates(availableProvider, 5000, 5, mAvaProListener);
        } else {

            mLocationManager.requestLocationUpdates(bestProvider, 5000, 5, mBestProListener);
            if (availableProvider != null) {
                mLocationManager.requestLocationUpdates(availableProvider, 5000, 5, mAvaProListener);
            } else {
                List<String> allProviders = mLocationManager.getAllProviders();
                for (String provider : allProviders) {
                    mLocationManager.requestLocationUpdates(provider, 0, 0, mBestProListener);
                }
            }
        }

        mLocationManager.addGpsStatusListener(mStarListener);// 星数变化回调

    }

    private void unregister() {
        mLocationManager.removeGpsStatusListener(mStarListener);
        mLocationManager.removeUpdates(mBestProListener);
        mLocationManager.removeUpdates(mAvaProListener);
    }

    // 提供器定位监听-------------------------------------
    private LocationListener mBestProListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            mTvDisplay.append("duration location way[" + location.getLatitude() + "," + location.getLongitude() + "]\n");
        }

        public void onProviderEnabled(String provider) {
            register();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private LocationListener mAvaProListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            mTvDisplay.append("duration location way[" + location.getLatitude() + "," + location.getLongitude() + "]\n");
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
            register();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    // ===========卫星状态监听器=====================
    private List<GpsSatellite> mSatelliteList = new ArrayList<>(); // 卫星信号
    private final GpsStatus.Listener mStarListener = new GpsStatus.Listener() {

        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            GpsStatus status = mLocationManager.getGpsStatus(null); // 取当前状态
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                mSatelliteList.clear();
                while (it.hasNext()) {
                    GpsSatellite s = it.next();
                    mSatelliteList.add(s);
                }
            }
            mTvDisplay.append("Satellite count[" + mSatelliteList.size() + "]\n");
        }
    };
    //==========单次定位监听器===================
    private LocationListener singleListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            onResult();
        }

        @Override
        public void onProviderEnabled(String provider) {
            onResult();
        }

        @Override
        public void onProviderDisabled(String provider) {
            onResult();
        }

        @Override
        public void onLocationChanged(Location location) {
            mTvDisplay.append("single location[" + location.getLatitude() + "," + location.getLongitude() + "]\n");
            onResult();
        }

        private void onResult() {
            mLocationManager.removeUpdates(singleListener);
            mBtnLocation.setText("Location");
        }
    };

}
