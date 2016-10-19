package com.yuexunit.android.library.library_utils.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

import com.yuexunit.android.library.library_utils.FileUtil;


public class L_Logger extends CustomLogger
{
    
    private String dir="";
    private String fileName="";
    private UploadE_api upUtil;
    
    private static class L_LogClass
    {
        
        private final static L_Logger instance=new L_Logger(CustomLogger.android_Context);
    }
    
    public static L_Logger getInstance() {
        return L_LogClass.instance;
    }
    
    private L_Logger(Context context){
        File root=FileUtil.getRootDir(context);
        if(root != null){
            dir=root.getAbsolutePath() ;
            fileName="L_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
    }
    
    /**
     * @Title: setDir
     * @Description:配置Log日志文件存储位置，默认应用程序根目录
     * @param dir
     *            必须是文件夹路径
     */
    public void setDir(String dir) {
        this.dir=dir;
    }
    
    @Override
    public void v(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveL_Log("v", tag, content, tr);
    }
    
    @Override
    public void d(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveL_Log("d", tag, content, tr);
    }
    
    @Override
    public void i(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveL_Log("i", tag, content, tr);
    }
    
    @Override
    public void w(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveL_Log("w", tag, content, tr);
    }
    
    @Override
    public void e(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveL_Log("e", tag, content, tr);
    }
    
    public String getFileName() {
        return fileName;
    }
    
    private void saveL_Log(String mark, String tag, String content, Throwable tr)
    {
        FileWriter fileWriter=null;
        try{
            File file=FileUtil.createFile(dir, fileName);
            fileWriter=new FileWriter(file, true);
            StringBuilder sb=new StringBuilder(System.getProperty("line.separator") + System.getProperty("line.separator") + "******" + mark + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "******");
            sb.append(System.getProperty("line.separator") + "LOCATION:" + tag);
            if( !TextUtils.isEmpty(content))
                sb.append(System.getProperty("line.separator") + "MESSAGE:" + content);
            if(null != tr)
                sb.append(System.getProperty("line.separator") + "ERROR:" + tr.getMessage());
            fileWriter.write(sb.toString());
            fileWriter.flush();
        } catch(IOException e){
            Logger.w(e, false);
        } finally{
            try{
                if(null != fileWriter)
                    fileWriter.close();
            } catch(IOException e){
                Logger.w(e, false);
            }
        }
    }
}
