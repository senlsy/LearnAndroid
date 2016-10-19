package com.yuexunit.android.library.library_utils.log;

import android.content.Context;


public abstract class CustomLogger
{
    
    public static Context android_Context=null;
    
    public abstract void v(String tag, String content, Throwable tr);
    
    public abstract void d(String tag, String content, Throwable tr);
    
    public abstract void i(String tag, String content, Throwable tr);
    
    public abstract void w(String tag, String content, Throwable tr);
    
    public abstract void e(String tag, String content, Throwable tr);
}