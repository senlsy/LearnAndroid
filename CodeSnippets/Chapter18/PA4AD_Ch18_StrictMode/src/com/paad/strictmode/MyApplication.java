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
                                                       // ��������滻ΪdetectAll()
                                                       // �Ͱ����˴��̶�д������I/O
                                                       .penaltyLog()
                                                       // ��ӡlogcat����ȻҲ���Զ�λ��dropbox��ͨ���ļ�������Ӧ��log
                                                       .build());
        StrictMode.setVmPolicy(
                  new StrictMode.VmPolicy.Builder()
                                                   .detectLeakedSqlLiteObjects()
                                                   .penaltyLog()
                                                   // ��ӡlogcat
                                                   .penaltyDeath()
                                                   .build());
    }
}