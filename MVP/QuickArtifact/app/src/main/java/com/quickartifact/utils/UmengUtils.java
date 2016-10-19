package com.quickartifact.utils;

import android.app.Activity;
import android.content.Context;

import com.quickartifact.utils.check.CheckUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Description: 友盟统计的帮助类
 *
 * @author liuranchao
 * @date 16/3/17 下午5:13
 */
public final class UmengUtils {

    private UmengUtils() {

    }

    public static void init(Context context) {
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        closeActivityDurationTrack();
    }

    /**
     * 禁止默认的页面统计方式，这样将不会再自动统计Activity。
     */
    private static void closeActivityDurationTrack() {
        try {
            MobclickAgent.openActivityDurationTrack(false);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * session的统计
     *
     * @param activity Activity
     */
    public static void onResume(Activity activity) {
        try {
            MobclickAgent.onResume(activity);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * session的统计
     *
     * @param activity Activity
     */
    public static void onPause(Activity activity) {
        try {
            MobclickAgent.onPause(activity);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * 统计页面开始
     *
     * @param pageName 页面名称
     */
    public static void onPageStart(String pageName) {
        if (CheckUtils.checkStrHasEmpty(pageName)) {
            return;
        }
        try {
            MobclickAgent.onPageStart(pageName);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * 统计页面结束
     *
     * @param pageName 页面名称
     */
    public static void onPageEnd(String pageName) {
        if (CheckUtils.checkStrHasEmpty(pageName)) {
            return;
        }
        try {
            MobclickAgent.onPageEnd(pageName);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * 自定义事件统计
     *
     * @param activity Activity
     * @param eventId  自定义事件的ID
     */
    public static void onEvent(Activity activity, String eventId) {
        try {
            MobclickAgent.onEvent(activity, eventId);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    /**
     * 设置是否使用集成测试
     *
     * @param debugMode
     */
    public static void setDebugMode(boolean debugMode) {
        try {
            MobclickAgent.setDebugMode(debugMode);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    public static void onKillProcess(Context context) {
        try {
            MobclickAgent.onKillProcess(context);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }

    }


}
