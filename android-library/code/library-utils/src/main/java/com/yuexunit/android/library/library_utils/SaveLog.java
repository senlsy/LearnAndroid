package com.yuexunit.android.library.library_utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yuexunit.android.library.library_utils.log.L_Logger;
import com.yuexunit.android.library.library_utils.log.Logger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


/**
 * @author LiuZR
 * @Description 保存错误日志（将错误日志存入SD卡）
 *
 */
public class SaveLog
{
    
    /**
     * 保存log的文件
     */
    public static File file4Log;
    /**
     * 保存exception的文件
     */
    public static File file4Exception;
    
    private static String getApplicationName(Context context) {
        PackageManager packageManager=null;
        ApplicationInfo applicationInfo=null;
        try{
            packageManager=context.getPackageManager();
            applicationInfo=packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException e){
            Logger.e("", e);
            Logger.e("", e);
        }
        String applicationName=(String)packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }
    
    private static File getRootDir(Context context) {
        try{
            File rootDir=context.getExternalFilesDir(null);
            if(rootDir == null)
                rootDir=context.getFilesDir();
            if(rootDir == null)
                throw new Exception("获取应用程序所属根目录异常");
            return rootDir;
        } catch(Exception e){
            Logger.e("", e);
            return null;
        }
    }
    
    /**
     * 捕获异常信息并保存
     * 
     * @param exception
     * @param context
     */
    public static void saveException(Exception exception, Context context) {
        if(file4Exception == null){
            File rootdir=getRootDir(context);
            if(rootdir == null)
                return;
            File logFileDir=new File(rootdir.getAbsolutePath() + File.separator + "Log4e");
            if( !logFileDir.exists())
                logFileDir.mkdirs();
            String fileName="error4" + getApplicationName(context);
            file4Exception=new File(logFileDir, fileName);
            if( !file4Exception.exists()){
                try{
                    file4Exception.createNewFile();
                } catch(Exception e){
                    Logger.e("", e);
                    return;
                }
            }
        }
        StackTraceElement caller=Logger.getCurrentStackTraceElement();
        String tag=generateTag(caller);
        FileWriter write=null;
        try{
            write=new FileWriter(file4Exception, true);
            PrintWriter writer=new PrintWriter(write);
            writer.append(System.getProperty("line.separator") + "***********"
                          + new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date()) + "***********"
                          + System.getProperty("line.separator") + "***********" + exception.getMessage() + "***********"
                          + System.getProperty("line.separator") + "******" + tag
                          + "******");
            writer.append(System.getProperty("line.separator"));// 换行
            exception.printStackTrace(writer);
            writer.flush();
        } catch(IOException e){
            Logger.e("", e);
        } finally{
            try{
                if(write != null)
                    write.close();
            } catch(IOException e){
                Logger.e("", e);
            }
        }
    }
    
    /**
     * 保存日志信息
     * 
     * @param msg
     * @param context
     */
    public static void saveLog(String msg, Context context) {
        if(file4Log == null){
            File rootdir=getRootDir(context);
            if(rootdir == null)
                return;
            File logFileDir=new File(rootdir.getAbsolutePath()
                                     + File.separator + "Log4e");
            if( !logFileDir.exists())
                logFileDir.mkdirs();
            String fileName="log4" + getApplicationName(context);
            file4Log=new File(logFileDir, fileName);
            if( !file4Log.exists()){
                try{
                    file4Log.createNewFile();
                } catch(Exception e){
                    Logger.e("", e);
                    return;
                }
            }
        }
        StackTraceElement caller=Logger.getCurrentStackTraceElement();
        String tag=generateTag(caller);
        FileWriter write=null;
        try{
            write=new FileWriter(file4Log, true);
            write.append(System.getProperty("line.separator") + "**************" + new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date()) + "**************"
                         + System.getProperty("line.separator") + "******" + tag + "******");
            write.append(System.getProperty("line.separator") + msg);
            write.flush();
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                if(write != null)
                    write.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 保存日志信息
     * 
     * @param dir
     *            文件位置
     * @param msg
     * @param context
     */
    public static void saveLog(String dir, String msg, Context context) {
        File logFileDir=null;
        if(TextUtils.isEmpty(dir)){
            File rootdir=getRootDir(context);
            if(rootdir == null)
                return;
            logFileDir=new File(rootdir.getAbsolutePath());
        }
        else{
            logFileDir=new File(dir);
        }
        if( !logFileDir.exists())
            logFileDir.mkdirs();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName="E_" + sdf.format(date);
        file4Log=new File(logFileDir, fileName);
        if( !file4Log.exists()){
            try{
                file4Log.createNewFile();
            } catch(Exception e){
                Logger.e("", e);
                return;
            }
        }
        FileWriter write=null;
        try{
            write=new FileWriter(file4Log, true);
            write.append(System.getProperty("line.separator") + msg);
            write.flush();
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                if(write != null)
                    write.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    private static String generateTag(StackTraceElement caller) {
        String tag="%s.%s(L:%d)";
        String callerClazzName=caller.getClassName();
        callerClazzName=callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag=String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }
}
