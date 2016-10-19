package com.quickartifact.utils.check;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.Collection;

/**
 * Description: 检查工具类
 *
 * @author mark.lin
 * @date 2016/9/12 17:32
 */
public final class CheckUtils {

    private CheckUtils() {
    }

    /**
     * 检查bitmap是否可用
     *
     * @param bitmap 被检查的bitmap
     */
    public static boolean checkBitmapAvailable(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    /**
     * 检查文件file是否存在。
     *
     * @param file 被检查的file
     */
    public static boolean checkFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 检查传递进来的参数是否有null
     *
     * @param parmaeters 被检查的参数数组
     */
    public static boolean checkParameterHasNull(Object... parmaeters) {
        if (parmaeters == null) {
            return true;
        }

        for (Object object : parmaeters) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查传递进来的string字符串是否含有null或空字符串
     *
     * @param parmaeters 被检查的String数组
     */
    public static boolean checkStrHasEmpty(String... parmaeters) {
        if (parmaeters == null) {
            return true;
        }
        for (String object : parmaeters) {
            if (object == null || TextUtils.isEmpty(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查集合是否为空
     *
     * @param collection 被检查的集合
     */
    public static boolean checkCollectionIsEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 外部存储是否可写状态
     */
    public static boolean checkExternalStoreWriteable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 外部存储是否可用状态，如需要写,判断checkExternalStoreWriteable()
     */
    public static boolean checkExternalStoreAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        } else {
            return false;
        }
    }


}
