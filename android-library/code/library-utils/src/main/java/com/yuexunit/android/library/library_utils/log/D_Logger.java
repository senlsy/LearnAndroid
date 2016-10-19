package com.yuexunit.android.library.library_utils.log;

import android.text.TextUtils;
import android.util.Log;


public class D_Logger extends CustomLogger
{
    
    private static class D_LogClass
    {
        
        private final static D_Logger instance=new D_Logger();
    }
    
    public static D_Logger getInstance() {
        return D_LogClass.instance;
    }
    
    private D_Logger(){
        super();
    }
    
    @Override
    public void v(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(content))
            content="";
        if(null == tr)
            Log.v(tag, content);
        else
            Log.v(tag, content, tr);
    }
    
    @Override
    public void d(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(content))
            content="";
        if(null == tr)
            Log.d(tag, content);
        else
            Log.d(tag, content, tr);
    }
    
    @Override
    public void i(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(content))
            content="";
        if(null == tr)
            Log.i(tag, content);
        else
            Log.i(tag, content, tr);
    }
    
    @Override
    public void w(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(content))
            content="";
        if(null == tr)
            Log.w(tag, content);
        else
            Log.w(tag, content, tr);
    }
    
    @Override
    public void e(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(content))
            content="";
        if(null == tr)
            Log.e(tag, content);
        else
            Log.e(tag, content, tr);
    }
}
