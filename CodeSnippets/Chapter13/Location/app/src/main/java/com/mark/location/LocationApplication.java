package com.mark.location;

import android.app.Application;

import com.mark.quick.base_library.ContextHolder;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2018/8/23 16:28
 * 修改备注：
 */
public class LocationApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.getInstance().initContext(getApplicationContext()).initDebug(BuildConfig.APPLICATION_ID, BuildConfig.DEBUG);
    }
}
