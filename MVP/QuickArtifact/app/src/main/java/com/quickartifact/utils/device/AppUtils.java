package com.quickartifact.utils.device;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.quickartifact.BaseApplication;

/**
 * Description: 获取应用程序相关信息。
 *
 * @author liuranchao
 * @date 16/3/17 上午10:52
 */
public final class AppUtils {

    private AppUtils() {

    }

    /**
     * 获得当前进程的名字
     *
     * @return 进程号
     */
    public static String getCurProcessName(String defaultStr) {

        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) BaseApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return defaultStr;


    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(String defaultStr) {
        try {
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(BaseApplication.getContext().getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return defaultStr;
        }
    }

    /**
     * 获取签名的MD5值
     */
    public static String getSignature(PackageInfo packageInfo) {
        String signature = null;
        if (packageInfo != null) {
            Signature[] signArr = packageInfo.signatures;
            if (signArr != null && signArr.length > 0) {
                signature = signArr[0].toCharsString();
            }
        }
        return signature;
    }


}
