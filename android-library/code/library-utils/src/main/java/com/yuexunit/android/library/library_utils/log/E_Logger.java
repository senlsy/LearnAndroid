package com.yuexunit.android.library.library_utils.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yuexunit.android.library.library_utils.DeviceInfoUtil;
import com.yuexunit.android.library.library_utils.FileUtil;
import com.yuexunit.android.library.library_utils.NetUtil;


public class E_Logger extends CustomLogger
{
    
    private String dir;
    private String upLoadUrl="http://192.168.10.23/fs/api/v1.0/uploadFile.json";
    private UploadE_api upUtil;
    
    private static final InternalHandler sHandler=getInstance().new InternalHandler();
    
    private static class E_LogClass
    {
        
        private final static E_Logger instance=new E_Logger(CustomLogger.android_Context);
    }
    
    public static E_Logger getInstance() {
        return E_LogClass.instance;
    }
    
    private E_Logger(Context context){
        File root=FileUtil.getRootDir(context);
        if(root != null){
            dir=root.getAbsolutePath();
            Logger.d("E_Logger=" + dir);
        }
    }
    
    public void setUpUtil(UploadE_api upUtil) {
        this.upUtil=upUtil;
    }
    
    /**
     * @Title: setUpLoadUrl
     * @Description:设置上传异常记录服务器url地址，默认地址为悦讯服务器地址。
     * @param upLoadUrl
     */
    public void setUpLoadUrl(String upLoadUrl) {
        this.upLoadUrl=upLoadUrl;
    }
    
    /**
     * @Title: setDir
     * @Description:配置异常记录存储位置，默认应用程序根目录
     * @param dir
     *            必须是文件夹路径
     * 
     */
    public void setDir(String dir) {
        this.dir=dir;
    }
    
    @Override
    public void v(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
    }
    
    @Override
    public void d(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
    }
    
    @Override
    public void i(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
    }
    
    @Override
    public void w(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
    }
    
    @Override
    public void e(String tag, String content, Throwable tr) {
        if(TextUtils.isEmpty(dir))
            return;
        saveE_Log(tag, content, tr);
    }
    
    private void saveE_Log(String tag, String content, Throwable tr) {
        // TODO 自动生成的方法存根
        FileWriter fileWrite=null;
        PrintWriter printWriter=null;
        try{
            Date date=new Date();
            File file4Log=FileUtil.createFile(dir, "E_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(date));
            if(file4Log == null)
                return;
            fileWrite=new FileWriter(file4Log);
            //
            HashMap<String, String> deviceInfo=DeviceInfoUtil.deviceInfo;
            String id=UUID.randomUUID().toString().replaceAll("-", "");
            deviceInfo.put("logId", id);
            deviceInfo.put("errorLocation", tag);
            deviceInfo.put("errorType", NetUtil.getCurrentNetType(android_Context));
            String ERROR_TIME=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            deviceInfo.put("errorTime", ERROR_TIME);
            if(null == tr){
                deviceInfo.put("errorType", null);
            }
            else{
                deviceInfo.put("errorType", tr.getClass().getSimpleName());// exception.getMessage()
            }
            String headStr=JSON.toJSONString(deviceInfo);
            fileWrite.write(headStr);
            //
            if( !TextUtils.isEmpty(content))
                fileWrite.write("\n\n" + content);
            //
            if(tr != null){
                StringWriter strWrite=new StringWriter();
                printWriter=new PrintWriter(strWrite);
                tr.printStackTrace(printWriter);
                printWriter.flush();
                fileWrite.write("\n\n" + strWrite.toString());
            }
            fileWrite.flush();
            Message msg=sHandler.obtainMessage();
            msg.obj=file4Log;
            msg.sendToTarget();
        } catch(Exception e){
            Logger.w(e, false);
        } finally{
            try{
                if(printWriter != null)
                    printWriter.close();
                if(fileWrite != null)
                    fileWrite.close();
            } catch(IOException e){
                Logger.w(e, false);
            }
        }
    }
    
    class InternalHandler extends Handler
    {
        
        private InternalHandler(){
            super(Looper.getMainLooper());
        }
        
        @Override
        public void handleMessage(Message msg) {
            File uploadFile=(File)msg.obj;
            if(uploadFile == null)
                return;
            if(upUtil != null)
                upUtil.uploadErrorFile(upLoadUrl, uploadFile);
            
        }
    }
}
