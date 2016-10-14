package com.paad.strictmode;

import android.app.Application;
import android.os.StrictMode;


/**
 * Listing 18-19: Enabling Strict Mode for an application
 */
public class MyApplication extends Application
{
    
    public static final boolean DEVELOPER_MODE=true;
    
    @Override
    public final void onCreate() {
        super.onCreate();
        // if(DEVELOPER_MODE){
        // StrictMode.enableDefaults();
        // }
        StrictMode.setThreadPolicy(
                  new StrictMode.ThreadPolicy.Builder()
                                                       .detectDiskReads()
                                                       .detectDiskWrites()
                                                       .detectNetwork()
                                                       // 这里可以替换为detectAll()
                                                       // 就包括了磁盘读写和网络I/O
                                                       .penaltyLog()
                                                       // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                                                       .build());
        StrictMode.setVmPolicy(
                  new StrictMode.VmPolicy.Builder()
                                                   .detectLeakedSqlLiteObjects()
                                                   .penaltyLog()
                                                   // 打印logcat
                                                   .penaltyDeath()
                                                   .build());
    }
}