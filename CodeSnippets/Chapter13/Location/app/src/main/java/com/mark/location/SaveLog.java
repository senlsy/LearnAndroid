package com.mark.location;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveLog {

    // 勿必获取读写sd卡权限
    static final String LogFileName = "lin4Log";

    private static final String ExecptionFileName = "lin4Exception";

    public static String getRootPath(Context context) {// 获取应用程序存储文件跟路径
        // if(context == null)
        // context=ProjectAppLaction.mContext;

        try {
            File rootDir = context.getExternalFilesDir(null);
            if (rootDir == null)
                rootDir = context.getFilesDir();
            if (rootDir == null)
                return null;
            return rootDir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveException(Exception exception, Context context) {
        String root = getRootPath(context);
        if (root == null)
            return;
        File sdDir = new File(root + File.separator + "exception");
        if (!sdDir.exists())
            sdDir.mkdirs();
        File exceptionFile = new File(sdDir, ExecptionFileName);
        if (!exceptionFile.exists()) {
            try {
                exceptionFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        FileWriter write = null;
        try {
            write = new FileWriter(exceptionFile, true);
            PrintWriter writer = new PrintWriter(write);
            writer.append(System.getProperty("line.separator")
                    + "***********"
                    + new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
                    .format(new Date()) + "***********"
                    + exception.getMessage() + "***********");
            writer.append(System.getProperty("line.separator"));// 换行
            exception.printStackTrace(writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (write != null)
                    write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveLog(String msg, Context context) {
        String root = getRootPath(context);
        if (root == null)
            return;
        File sdDir = new File(root + File.separator + "log");
        if (!sdDir.exists())
            sdDir.mkdirs();
        Log.e("lintest", sdDir.getAbsolutePath() + "---------");
        File logFile = new File(sdDir, LogFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        FileWriter write = null;
        try {
            write = new FileWriter(logFile, true);
            write.append(System.getProperty("line.separator")
                    + "**************"
                    + new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
                    .format(new Date()) + "**************");
            write.append(System.getProperty("line.separator") + msg);
            write.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (write != null)
                    write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
