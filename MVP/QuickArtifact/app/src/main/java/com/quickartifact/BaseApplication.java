package com.quickartifact;

import android.app.Application;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.multidex.MultiDex;

import com.quickartifact.manager.account.ActivityLifecycleManager;
import com.quickartifact.manager.crash.CrashHandler;
import com.quickartifact.utils.UmengUtils;
import com.quickartifact.utils.log.LogUtils;
import com.squareup.leakcanary.LeakCanary;

/**
 * Description: 基类Application，方便一些Utils取Context上下文。
 *
 * @author mark.lin
 * @date 2016/9/5 15:54
 */
public class BaseApplication extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.init();
        CrashHandler.getInstance().init();
        mContext = this.getApplicationContext();
        registerActivityLifecycleCallbacks(ActivityLifecycleManager.getInstance());
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
        UmengUtils.init(mContext);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
