package com.quickartifact.utils.log;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.quickartifact.BuildConfig;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.device.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

/**
 * Description: Log的帮助类
 * 开关根据是否为debug
 *
 * @author liuranchao
 * @date 16/3/13 上午12:13
 */
public final class LogUtils {

    private static final String TAG = AppUtils.getAppName("mark.lin");

    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void init() {
//        Logger.init(TAG)                 // default PRETTYLOGGER or use just init()
//                .methodCount(3)                 // default 2
//                .hideThreadInfo()               // default shown
//                .logLevel(LogLevel.NONE)        // default LogLevel.FULL
//                .methodOffset(2)                // default 0
//                .logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
        Logger.init(TAG).methodCount(1).methodOffset(1);
    }

    private LogUtils() {
    }


    //====================================
    // verbose
    //====================================

    /**
     * 输出v
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void v(String format, Object... args) {
        if (DEBUG) {
            Logger.v(format, args);
        }
    }

    /**
     * 输出v
     *
     * @param tag 标记
     * @param msg 输出呢呢绒
     */
    public static void v(String tag, String msg) {
        if (DEBUG) {
            setTag(tag);
            Logger.v(msg);
        }
    }

    //====================================
    // debug
    //====================================

    /**
     * 输出d
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void d(String format, Object... args) {
        if (DEBUG) {
            Logger.d(format, args);
        }
    }

    /**
     * 输出d
     *
     * @param tag 标记
     * @param msg 输出呢呢绒
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            setTag(tag);
            Logger.d(msg);
        }
    }

    //====================================
    // info
    //====================================

    /**
     * 输出i
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void i(String format, Object... args) {
        if (DEBUG) {
            Logger.i(format, args);
        }
    }

    /**
     * 输出i
     *
     * @param tag 标记
     * @param msg 输出呢呢绒
     */
    public static void i(String tag, String msg) {
        if (DEBUG) {
            setTag(tag);
            Logger.i(msg);
        }
    }

    //====================================
    // warn
    //====================================

    /**
     * 输出w
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void w(String format, Object... args) {
        if (DEBUG) {
            Logger.w(format, args);
        }
    }

    /**
     * 输出w
     *
     * @param tag 标记
     * @param msg 输出呢呢绒
     */
    public static void w(String tag, String msg) {
        if (DEBUG) {
            setTag(tag);
            Logger.w(msg);
        }
    }

    //====================================
    // error
    //====================================

    /**
     * 输出e
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void e(String format, Object... args) {
        if (DEBUG) {
            Logger.e(format, args);
        }
    }

    /**
     * 输出e
     *
     * @param tag 标记
     * @param msg 输出呢呢绒
     */
    public static void e(String tag, String msg) {
        if (DEBUG) {
            setTag(tag);
            Logger.e(msg);
        }
    }

    /**
     * 输出e
     *
     * @param throwable Throwable
     * @param format    格式化string
     * @param args      参数数组
     */
    public static void e(Throwable throwable, String format, Object... args) {
        if (DEBUG) {
            Logger.e(throwable, format, args);
        }
    }

    //=================WTF===================

    /**
     * 输出错误
     *
     * @param format 格式化string
     * @param args   参数数组
     */
    public static void wtf(String format, Object... args) {
        if (DEBUG) {
            Logger.wtf(format, args);
        }
    }

    /**
     * 打印crash
     *
     * @param throwable Throwable
     */
    public static void wtf(Throwable throwable) {
        if (DEBUG) {
            Log.wtf(TAG, throwable);
        }
    }

    public static void wtf(String msg, Throwable throwable) {
        if (DEBUG) {
            Log.wtf(TAG, msg, throwable);
        }
    }

    //=================JSON,Bundle,Intent===================

    /**
     * 输出JSON歌是
     *
     * @param jsonStr JSON字符串
     */
    public static void json(String jsonStr) {
        if (DEBUG && !CheckUtils.checkStrHasEmpty(jsonStr)) {
            try {
                Logger.json(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error error) {
                error.printStackTrace();
            }
            e("jsonStr serialize error!");
        } else {
            w("jsonStr is empty!");
        }
    }

    public static void logIntentExtra(Intent intent) {
        logBundle(intent.getExtras());
    }

    public static void logBundle(Bundle bundle) {
        if (bundle == null) {
            w("bundle==null");
            return;
        }

        Set<String> keySet = bundle.keySet();
        if (null == keySet || keySet.isEmpty()) {
            i("bundle no has value");
            return;
        }

        JSONObject jobj = new JSONObject();
        try {
            for (String key : keySet) {
                String value = bundle.get(key).toString();
                jobj.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json(jobj.toString());
    }

    //===============check=============

    public static void checkParameterNull(Object... parmaeters) {

        StringBuffer sb = new StringBuffer("parameters no null are：");
        for (Object object : parmaeters) {
            if (object != null) {
                sb.append(object.getClass().getSimpleName() + File.separator);
            }
        }
        i(sb.toString());
    }
    //=================private===================

    /**
     * 设置后的第一次调用有效
     *
     * @param tag Tag,拼接在默认的tag后面
     */
    private static void setTag(String tag) {
        com.orhanobut.logger.Logger.t(tag);
    }


}
