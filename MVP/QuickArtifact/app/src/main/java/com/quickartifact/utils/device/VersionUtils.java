package com.quickartifact.utils.device;

import android.os.Build;

/**
 * Description: Android API版本检查<p/>
 * Gingerbread  =9(Android 2.3)     姜饼<br/>
 * Honeycomb    =11(Android 3.0)    蜂巢<br/>
 * HoneycombMR1 =12(Android 3.1)    蜂巢<br/>
 * ICS          =14(Android 4.0)    冰激凌<br/>
 * ICSMR1       =15(Android 4.0.3)  冰激凌<br/>
 * Jellybean    =16(Android 4.1)    软心糖<br/>
 * JellybeanMR1 =17(Android 4.2)    软心糖<br/>
 * Kitkat       =19(Android 4.4)    巧克力<br/>
 * Lollipop     =21(Android 5.0)    棒棒糖<br/>
 * LollipopMR1  =22(Android 5.1)    棒棒糖<br/>
 * M            =23(Android 6.0)    棉花糖<br/>
 *
 * @author mark.lin
 * @date 2016/9/6 10:41
 */
public final class VersionUtils {

    private VersionUtils() {
    }


    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Version>=9(Android 2.3)
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= 9;//Build.VERSION_CODES.GINGERBREAD;
    }


    /**
     * Version>=11(Android 3.0)平板API
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;// Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Version>=12(Android 3.1)平板API
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= 12;// Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Version>=14(Android 4.0)
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= 14;// Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Version>=15(Android 4.0.3)
     */
    public static boolean hasICSMR1() {
        return Build.VERSION.SDK_INT >= 15;// Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * Version>=16(Android 4.1)
     */
    public static boolean hasJellybean() {
        return android.os.Build.VERSION.SDK_INT >= 16;//android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Version>=17(Android 4.2)
     */
    public static boolean hasJellybeanMR1() {
        return android.os.Build.VERSION.SDK_INT >= 17;// Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * Version>=19(Android 4.4)
     */
    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= 19;// Build.VERSION_CODES.KITKAT;
    }

    /**
     * Version>=21(Android 5.0)
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= 21;// Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Version>=22(Android 5.1)
     */
    public static boolean hasLollipopMR1() {
        return Build.VERSION.SDK_INT >= 22;//Build.VERSION_CODES.LOLLIPOP_MR1;
    }


    /**
     * Version>=23(Android 6.0)
     */
    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= 23;//Build.VERSION_CODES.M;
    }
}
