package com.paad.earthquake;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;


public class SaveLog
{
    
    private static final String LogFileName="lin4Log";
    
    private static final String ExecptionFileName="lin4Exception";
    
    public static String getRootPath(Context context)
    {// 获取应用程序存储文件跟路径
     // if(context == null)
     // context=ProjectAppLaction.mContext;
        File rootDir=null;
        boolean ExternalStroeExist=Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if(ExternalStroeExist)
        {
            rootDir=context.getExternalFilesDir(null);
        }
        else
        {
            rootDir=context.getCacheDir();
            if(rootDir == null)
                rootDir=context.getDir("file", Context.MODE_PRIVATE);
        }
        if(rootDir == null)
            return null;
        else
            return rootDir.getAbsolutePath();
    }
    
    public static void saveException(Exception exception, Context context)
    {
        File sdDir=new File(getRootPath(context) + File.separator + "exception");
        if(sdDir == null)
            return;
        if( !sdDir.exists())
            sdDir.mkdirs();
        File exceptionFile=new File(sdDir, ExecptionFileName);
        if( !exceptionFile.exists()){
            try{
                exceptionFile.createNewFile();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        FileWriter write=null;
        try{
            write=new FileWriter(exceptionFile, true);
            PrintWriter writer=new PrintWriter(write);
            writer.append(System.getProperty("line.separator") +
                          "*************************" +
                          new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date()) +
                          "********" +
                          exception.getMessage() +
                          "*************************");
            writer.append(System.getProperty("line.separator"));// 换行
            exception.printStackTrace(writer);
            writer.flush();
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
    
    public static void saveLog(String msg, Context context) {
        File sdDir=new File(getRootPath(context) + File.separator + "log");
        if(sdDir == null)
            return;
        if( !sdDir.exists())
            sdDir.mkdirs();
        File logFile=new File(sdDir, LogFileName);
        if( !logFile.exists()){
            try{
                logFile.createNewFile();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        FileWriter write=null;
        try{
            write=new FileWriter(logFile, true);
            write.append(System.getProperty("line.separator") +
                         "*************************" +
                         new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date()) +
                         "*************************");
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
}
