package com.quickartifact.utils.device;


import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.quickartifact.BaseApplication;

/**
 * Description: 操作资源文件的工具类
 *
 * @author liuranchao
 * @date 16/3/13 上午10:54
 */
public final class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * 获取arrays.xml中的字符串数组
     *
     * @param arrId Resource id  R.array.xxx
     * @return string数组
     */
    public static String[] getStringArray(@ArrayRes int arrId) {
        return BaseApplication.getContext().getResources().getStringArray(arrId);
    }

    /**
     * 获取arrays.xml中的int数组
     *
     * @param arrId Resource id  R.array.xxx
     * @return int数组
     */
    public static int[] getIntArray(@ArrayRes int arrId) {
        return BaseApplication.getContext().getResources().getIntArray(arrId);
    }

    /**
     * 获取strings.xml的文本
     *
     * @param resId      strings.xml
     * @param formatArgs 替换的参数
     * @return String
     */
    public static String getString(@StringRes int resId, Object... formatArgs) {
        return BaseApplication.getContext().getString(resId, formatArgs);
    }

    /**
     * 获取color 颜色
     *
     * @param colorId colors.xml
     * @return color
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static int getColor(@ColorRes int colorId) {
        if (VersionUtils.hasM()) {
            return BaseApplication.getContext().getResources().getColor(colorId, BaseApplication.getContext().getTheme());
        } else {
            return BaseApplication.getContext().getResources().getColor(colorId);
        }
    }

    /**
     * 获取大小 dimen
     *
     * @param dimenId dimens.xml
     * @return 像素
     */
    public static float getDimen(@DimenRes int dimenId) {
        return BaseApplication.getContext().getResources().getDimensionPixelSize(dimenId);
    }

    /**
     * 获取drawable
     *
     * @param drawableId res下的drawable资源
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(@DrawableRes int drawableId) {
        if (VersionUtils.hasLollipop()) {
            return BaseApplication.getContext().getResources().getDrawable(drawableId, BaseApplication.getContext().getTheme());
        } else {
            return BaseApplication.getContext().getResources().getDrawable(drawableId);
        }

    }
}
