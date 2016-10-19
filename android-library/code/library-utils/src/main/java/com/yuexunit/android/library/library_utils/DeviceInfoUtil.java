package com.yuexunit.android.library.library_utils;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.yuexunit.android.library.library_utils.log.CustomLogger;


public class DeviceInfoUtil
{
    
    public static HashMap<String, String> deviceInfo=null;
    
    public static void initDevcieInfo(Context context) throws NameNotFoundException
    {
        if(deviceInfo == null)
            deviceInfo=new HashMap<String, String>();
        CustomLogger.android_Context=context;
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(), 0);
        int APP_VERSION=packageInfo.versionCode;
        ApplicationInfo applicationInfo=packageManager.getApplicationInfo(context.getPackageName(), 0);
        String APP_NAME=(String)packageManager.getApplicationLabel(applicationInfo);
        deviceInfo.put("appName", APP_NAME);
        deviceInfo.put("appVersion", "" + APP_VERSION);
        deviceInfo.put("platform", "Android");
        deviceInfo.put("platformVersion", Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT);
        deviceInfo.put("phoneModel", Build.BRAND + " " + Build.MODEL);
        TelephonyManager localTelephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        switch(localTelephonyManager.getPhoneType())
        {
            case TelephonyManager.PHONE_TYPE_CDMA:
                deviceInfo.put("phoneType", "CDMA");
                deviceInfo.put("phoneId", localTelephonyManager.getDeviceId());
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                deviceInfo.put("phoneType", "GSM");
                deviceInfo.put("phoneId", localTelephonyManager.getDeviceId());
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                deviceInfo.put("phoneType", "SIP");
                deviceInfo.put("phoneId", localTelephonyManager.getDeviceId());
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                deviceInfo.put("phoneType", "UNKNOW");
                deviceInfo.put("phoneId", "UNKNOW");
                break;
        }
    }
}
