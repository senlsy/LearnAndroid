/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuexunit.android.library.library_utils.log;

import com.yuexunit.android.library.library_utils.DeviceInfoUtil;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;


/**
 * Log工具，类似android.util.Log。 tag自动产生，格式:
 * customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * <p/>
 * Author: wyouflf Date: 13-7-24 Time: 下午12:23
 */
public class Logger
{
    
    public static boolean useDefaultTag=false;// 是否使用默认tag标签
    public static String customTagPrefix="";// tag标签前缀
    public static String defaultTag="lintest";// 默认使用的tag标签
    //
    private static boolean D_Debug=true;// 输出LogCat
    private static boolean L_Debug=false;// 持久化LOG记录
    private static boolean E_Debug=false;// 持久化Exception记录
    //
    private static CustomLogger d_Logger;
    private static CustomLogger e_Logger;
    private static CustomLogger l_Logger;
    static{
        d_Logger=D_Logger.getInstance();
    }
    
    /**
     * @Title: initLogSystem
     * @Description:初始化日志打印系统
     * @param context
     * @param d_Debug
     *            是否启用Locat调试模式
     * @param l_Debug
     *            是否启用日志记录存储
     * @param e_Debug
     *            是否启用异常记录存储
     * @param upUtil
     *            上传的工具类，默认实现在library-http下的UpLoadE_impl.class
     * @throws NameNotFoundException
     */
    public static void initLogSystem(Context context, boolean d_Debug, boolean l_Debug, boolean e_Debug, UploadE_api upUtil) throws NameNotFoundException
    {
        D_Debug=d_Debug;
        L_Debug=l_Debug;
        E_Debug=e_Debug;
        DeviceInfoUtil.initDevcieInfo(context);
        CustomLogger.android_Context=context;
        if(D_Debug)
            d_Logger=D_Logger.getInstance();
        if(L_Debug)
            l_Logger=L_Logger.getInstance();
        if(E_Debug){
            E_Logger temp=E_Logger.getInstance();
            temp.setUpUtil(upUtil);
            e_Logger=temp;
        }
    }
    
    public static void v(String content)
    {
        v(content, false);
    }
    
    public static void v(String content, boolean record) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.v(useDefaultTag ? defaultTag : tag, content, null);
        if(L_Debug && record)
            l_Logger.v(tag, content, null);
    }
    
    public static void d(String content) {
        d(content, false);
    }
    
    public static void d(String content, boolean record) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.d(useDefaultTag ? defaultTag : tag, content, null);
        if(L_Debug && record)
            l_Logger.d(tag, content, null);
    }
    
    public static void i(String content) {
        i(content, false);
    }
    
    public static void i(String content, boolean record) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.i(useDefaultTag ? defaultTag : tag, content, null);
        if(L_Debug && record)
            l_Logger.i(tag, content, null);
    }
    
    public static void w(String content) {
        w(content, false);
    }
    
    public static void w(String content, boolean recorder) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.w(useDefaultTag ? defaultTag : tag, content, null);
        if(L_Debug && recorder)
            l_Logger.w(tag, content, null);
    }
    
    public static void w(Throwable tr, boolean recorder)
    {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.w(useDefaultTag ? defaultTag : tag, null, tr);
        if(L_Debug && recorder)
            l_Logger.w(tag, null, tr);
    }
    
    public static void w(String content, Throwable tr, boolean recorder)
    {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.w(useDefaultTag ? defaultTag : tag, content, tr);
        if(L_Debug && recorder)
            l_Logger.w(tag, content, tr);
    }
    
    public static void e(String content) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, content, null);
        if(E_Debug)
            e_Logger.e(tag, content, null);
    }
    
    public static void e(String content, boolean recorder) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, content, null);
        if(L_Debug && recorder)
            l_Logger.e(tag, content, null);
        if(E_Debug)
            e_Logger.e(tag, content, null);
    }
    
    public static void e(Throwable tr) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, null, tr);
        if(E_Debug)
            e_Logger.e(tag, null, tr);
    }
    
    public static void e(Throwable tr, boolean recorder) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, null, tr);
        if(L_Debug && recorder)
            l_Logger.e(tag, null, tr);
        if(E_Debug)
            e_Logger.e(tag, null, tr);
    }
    
    public static void e(String content, Throwable tr) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, content, tr);
        if(E_Debug)
            e_Logger.e(tag, content, tr);
    }
    
    public static void e(String content, Throwable tr, boolean recorder) {
        String tag=generateTag();
        if(D_Debug)
            d_Logger.e(useDefaultTag ? defaultTag : tag, content, tr);
        if(L_Debug && recorder)
            l_Logger.e(tag, content, tr);
        if(E_Debug)
            e_Logger.e(tag, content, tr);
    }
    
    // -----------------------------------------------
    private static String generateTag() {
        StackTraceElement caller=getCurrentStackTraceElement();
        String tag=formatTag(caller);
        return tag;
    }
    
    private static String formatTag(StackTraceElement caller) {
        String tag="%s.%s(L:%d)";
        String callerClazzName=caller.getClassName();
        callerClazzName=callerClazzName.substring(callerClazzName
                                                                 .lastIndexOf(".") + 1);
        tag=String.format(tag, callerClazzName, caller.getMethodName(),
                          caller.getLineNumber());
        tag=TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }
    
    public static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[5];
    }
    
    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[6];
    }
    // ----------------------------------------------
}
