package com.quickartifact.utils.device;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Description: 软键盘工具类
 *
 * @author ouyangzx
 * @date 16/4/11 下午8:46
 */
public final class SoftKeyboardUtils {
    private SoftKeyboardUtils() {
    }


    /**
     * 关闭软键盘
     */
    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 显示键盘，延时操作
     *
     * @param view    View
     * @param context Context
     */
    public static void showDelay(final View view, final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300);
    }

}
