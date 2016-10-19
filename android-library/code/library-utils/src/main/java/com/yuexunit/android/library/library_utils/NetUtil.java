package com.yuexunit.android.library.library_utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


public class NetUtil
{
    
    /**
     * @Title: getCurrentNetType
     * @Description:获取设备所处的网络坏境
     * @param context
     * @return
     */
    public static String getCurrentNetType(Context context) {
        String type="";
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        if(info == null){
            type="UNKNOW";
        }
        else if(info.getType() == ConnectivityManager.TYPE_WIFI){
            type="wifi";
        }
        else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int subType=info.getSubtype();
            if(subType == TelephonyManager.NETWORK_TYPE_CDMA
               || subType == TelephonyManager.NETWORK_TYPE_GPRS
               || subType == TelephonyManager.NETWORK_TYPE_EDGE){
                type="2g";
            }
            else if(subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B){
                type="3g";
            }
            else if(subType == TelephonyManager.NETWORK_TYPE_LTE){// LTE是3g到4g的过渡，是3.9G的全球标准
                type="4g";
            }
        }
        return type;
    }
}
