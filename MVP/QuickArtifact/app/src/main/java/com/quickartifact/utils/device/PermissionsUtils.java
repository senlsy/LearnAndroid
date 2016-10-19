package com.quickartifact.utils.device;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.quickartifact.BaseApplication;
import com.quickartifact.exception.runtime.LackPermissionException;
import com.quickartifact.utils.StringUtils;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.log.LogUtils;

import java.util.ArrayList;

/**
 * Description: 权限检查工具
 *
 * @author mark.lin
 * @date 2016/9/6 10:36
 */
public final class PermissionsUtils {

    private PermissionsUtils() {
    }

    /***
     * 申请权限
     */
    private static void requestPermissions(int requestCode, Activity activity, @NonNull PermissionsEnum... permissions) {
        String[] strArray = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            strArray[i] = permissions[i].getKey();
        }
        ActivityCompat.requestPermissions(activity, strArray, requestCode);
    }


    //=============================================
    //提供给外部使用
    //=============================================

    /**
     * app缺失某一权限
     *
     * @param permissions 权限
     * @return 缺失true，不缺失false
     */
    private static boolean lacksPermissions(@NonNull PermissionsEnum... permissions) {
        if (!isNeedCheck()) {
            return false;
        }

        Context context = BaseApplication.getContext();
        for (PermissionsEnum permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission.getKey()) == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }

    /***
     * 拒绝所有权限且都不再提示
     *
     * @param activity    activity上下文
     * @param permissions 检查的权限
     * @return 缺失全部且不再提示返回ture，其他返回false
     */
    private static boolean hasDelayAllPermissions(Activity activity, @NonNull PermissionsEnum... permissions) {

        if (!isNeedCheck()) {
            return false;
        }

        int count = 0;
        for (PermissionsEnum permission : permissions) {
            if (lacksPermissions(permission) &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.getKey())) {
                //拒绝且不再提示
                count++;
            }
        }
        if (count == permissions.length) {
            return true;
        }
        return false;
    }

    /**
     * 权限检查，建议在Activity一开始进行检查。
     *
     * @param activity    上下文
     * @param permissions 需要申请的权限
     */
    public static void checkPermission(int requestCode, Activity activity, @NonNull PermissionsEnum... permissions) {

        if (!isNeedCheck()) {
            return;
        }
        if (CheckUtils.checkParameterHasNull(activity, permissions)) {
            return;
        }
        if (PermissionsUtils.lacksPermissions(permissions)) {
            if (PermissionsUtils.hasDelayAllPermissions(activity, permissions)) {
                //TODO:弹窗提示权限缺失，可能导致某部分功能无法使用，并可以去导航去设置页面设置。
                IntentUtils.startAppSetting(activity);
            } else {
                //请求所有权限，不管已通过的或未通过的
                requestPermissions(requestCode, activity, permissions);
            }

        }

    }


    /**
     * 在activity申请权限之后，原参数回调即可
     */
    public static void onRequestPermissionsResult(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (!isNeedCheck()) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        ArrayList<PermissionsEnum> lackPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                PermissionsEnum permission = PermissionsEnum.getPermissionEnum(permissions[i]);
                lackPermissions.add(permission);
                sb.append(StringUtils.format("lack permission %s:%s", permission.getDescription(), permission.getKey()));
                sb.append(System.getProperty("line.separator"));
            }
        }


        if (!CheckUtils.checkCollectionIsEmpty(lackPermissions)) {
            FileUtils.wirteExceptionToFile(new LackPermissionException(sb.toString()));//当成异常记录下来
            LogUtils.e(sb.toString());
            //TODO:弹窗提示权限缺失，可能导致某部分功能无法使用，并可以去导航去设置页面设置。
            IntentUtils.startAppSetting(activity);
        }


    }

    /**
     * 是否需要检查,android api>5.0需要检查
     *
     * @return 检查true，不需要检查false
     */
    private static boolean isNeedCheck() {
        if (VersionUtils.hasLollipop()) {//5.0需要运行时检查
            return true;
        }
        return false;
    }

}
