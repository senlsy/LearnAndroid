package com.quickartifact.utils.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.quickartifact.BaseApplication;
import com.quickartifact.utils.log.LogUtils;
import com.quickartifact.utils.signature.SignatureUtils;
import com.quickartifact.utils.signature.SignatureEnum;

import java.util.UUID;

/**
 * Description: 获取硬件设备的相关信息
 *
 * @author mark.lin
 * @date 16/3/17 上午9:42
 */
public final class DeviceUtils {

    private DeviceUtils() {
    }

    /**
     * 获取屏幕的信息，是经过系统调整过后的屏幕信息<br/>
     * 例如：系统增加了虚拟导航栏，屏幕高度就扣除了虚拟导航栏的高度
     */
    public static void printWindInfo() {

        WindowManager windowManager = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        //outMetrics = context.getResources().getDisplayMetrics();

        LogUtils.i("width x height =%s \n" +
                        "density =%s \n" +
                        "densityDpi=%s \n" +
                        "xdpi=%s \n" +
                        "ydpi=%s \n",
                outMetrics.widthPixels + "x" + outMetrics.heightPixels,
                outMetrics.density,
                outMetrics.densityDpi,
                outMetrics.xdpi,
                outMetrics.ydpi

        );
       /* outMetrics.widthPixels;//屏幕宽
        outMetrics.heightPixels;// 不包括虚拟导航栏的高度,包括状态栏
        outMetrics.density;//像素密度比 dp:px=density
        outMetrics.densityDpi;//像素密度dpi，就近原则加载资源限定符120dpi(ldpi)-160dpi(mdpi)-240dpi(hdpi)-320dpi(xhdpi)-480dip(xxhdip)-640dip(xxxhdpi)
        outMetrics.scaledDensity;//缩放倍数dp:px=scaledDensity
        outMetrics.xdpi;//在x轴上每英寸包含的像素点
        outMetrics.ydpi);//在y轴上每英寸包含的像素点*/


    }


    /**
     * 获取屏幕宽高
     *
     * @return Point x: width y : height
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenWH() {

        WindowManager windowManager = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        if (VersionUtils.hasGingerbread()) {
            display.getSize(point);
        } else {
            point = new Point(display.getWidth(), display.getHeight());
        }
        return point;
    }

    /**
     * 获取mac地址<br/>
     * 需要申请全选:ACCESS_WIFI_STATE
     */
    private static String getMacAddress(String defaultStr) {
        try {
            WifiManager wifi = (WifiManager) BaseApplication.getContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultStr;
        }
    }

    /**
     * 获得去掉冒号的macaddress<br/>
     * 需要申请全选:ACCESS_WIFI_STATE
     */
    private static String getClearlyMacAddress(String defaultStr) {
        String macAddress = getMacAddress(defaultStr);
        return macAddress.replace(":", "");
    }

    /**
     * 获取设备唯一标识:android id + mac + serial number
     */
    public static String getUniqueID() {
        String androidId = getAndroidId();
        String macAddress = getClearlyMacAddress("unknow");
        String serialNumber = getSerialNumber();
        return SignatureUtils.encoding(androidId + macAddress + serialNumber, SignatureEnum.MD5);
    }

    public static String generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 获取android id
     */
    private static String getAndroidId() {
        Context context = BaseApplication.getContext();
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备串号
     */
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 获取系统版本号
     */
    public static String getAndroidApiVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备型号
     */
    public static String getPhoneMode() {
        return Build.BRAND + " " + Build.MODEL;
    }

    /**
     * 获取平台名称:Android
     */
    public static String getPlatfrom(String defaultStr) {
        return defaultStr;
    }


    /**
     * 获取设备id
     * 需要申请权限:READ_PHONE_STATE
     */
    public static String getDeviceId(String defualtStr) {
        try {
            Context context = BaseApplication.getContext();
            TelephonyManager localTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (localTelephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                return localTelephonyManager.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return defualtStr;

    }


}
