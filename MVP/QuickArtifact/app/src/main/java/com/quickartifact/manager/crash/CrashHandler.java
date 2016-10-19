package com.quickartifact.manager.crash;

import com.quickartifact.BaseApplication;
import com.quickartifact.utils.UmengUtils;
import com.quickartifact.utils.device.SuperUtils;
import com.quickartifact.utils.file.FileUtils;

/**
 * Description: 未捕获异常处理。
 * 1、将异常记录到日志文件
 *
 * @author mark.lin
 * @date 2016/9/13 14:55
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private CrashHandler() {
    }

    private static class HolderClass {
        private final static CrashHandler instance = new CrashHandler();
    }

    public static CrashHandler getInstance() {
        return HolderClass.instance;
    }

    public void init() {
        //处理未捕获的异常
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        toTo(ex);
    }

    /**
     * 未捕捉的异常，执行日志记录
     *
     * @param ex
     */
    public synchronized void toTo(Throwable ex) {
        FileUtils.wirteExceptionToFile(ex);
        SuperUtils.killProcess(BaseApplication.getContext().getPackageName());
        UmengUtils.onKillProcess(BaseApplication.getContext());
    }


}
