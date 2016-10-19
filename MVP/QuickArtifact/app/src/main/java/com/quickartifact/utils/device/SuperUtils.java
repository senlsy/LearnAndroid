package com.quickartifact.utils.device;

import com.quickartifact.utils.file.FileUtils;

import java.io.DataOutputStream;

/**
 * Description: 获取root权限的工具类
 *
 * @author mark.lin
 * @date 2016/9/13 13:20
 */
public final class SuperUtils {

    private SuperUtils() {
    }

    /**
     * 执行adb shell 命令
     *
     * @param command 命令语句
     */
    private static void startAmCommand(String command) {
        DataOutputStream out = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            out = new DataOutputStream(process.getOutputStream());

            out.writeBytes(command + " \n");

            out.writeBytes("exit\n");
            out.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(out);
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 根据packageNmae 结束进程
     *
     * @param packageName 应用报名
     */
    public static void killProcess(String packageName) {
        startAmCommand("am force-stop " + packageName);
    }


    /**
     * 获取该路径可读权限
     */
    public static void getPathRoot(String path) {
        startAmCommand("chmod 777 " + path);
    }
}
