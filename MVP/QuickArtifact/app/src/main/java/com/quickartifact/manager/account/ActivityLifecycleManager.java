package com.quickartifact.manager.account;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Description: 所有Activity 生命周期管理
 *
 * @author ouyangzx
 * @date 16/7/29 下午4:33
 */
public final class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {
    private static ActivityLifecycleManager sInstance;

    private int mActivityCount;


    private ActivityLifecycleManager() {
    }

    /**
     * @return getInstance()
     */
    public static ActivityLifecycleManager getInstance() {
        synchronized (ActivityLifecycleManager.class) {
            if (sInstance == null) {
                sInstance = new ActivityLifecycleManager();
            }
        }
        return sInstance;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isForegroundProcess() {
        return mActivityCount > 0;
    }
}
