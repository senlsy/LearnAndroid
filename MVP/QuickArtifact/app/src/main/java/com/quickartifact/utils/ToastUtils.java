package com.quickartifact.utils;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.quickartifact.BaseApplication;

/**
 * Description: Toast帮助类
 *
 * @author liuranchao
 * @date 16/3/15 下午2:51
 */
public final class ToastUtils {

    private ToastUtils() {
    }

    /**
     * 显示系统的toast
     *
     * @param resId strings.xml
     */
    public static void show(@StringRes int resId) {
        Toast.makeText(BaseApplication.getContext(), resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示系统的toast
     *
     * @param text CharSequence
     */
    public static void show(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_LONG).show();
        }
    }
}
