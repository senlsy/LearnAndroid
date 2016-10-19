package com.quickartifact.exception;

import com.quickartifact.utils.file.FileUtils;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Description: TODO
 *
 * @author mark.lin
 * @date 2016/9/14 11:30
 */
public abstract class BaseCheckException extends Exception {

    protected abstract String getTag();

    private boolean isLog = true;

    public BaseCheckException() {
    }

    public BaseCheckException(String detailMessage) {
        super(detailMessage);
    }

    public BaseCheckException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BaseCheckException(Throwable throwable) {
        super(throwable);
    }


    @Override
    public void printStackTrace(PrintWriter err) {
        err.append(getTag());
        err.append(System.getProperty("line.separator"));
        super.printStackTrace(err);
        if (isLog) {
            FileUtils.writeLogToFile(getTag());
        }
    }

    @Override
    public void printStackTrace(PrintStream err) {
        err.append(getTag());
        err.append(System.getProperty("line.separator"));
        super.printStackTrace(err);
        if (isLog) {
            FileUtils.writeLogToFile(getTag());
        }
    }

    public BaseCheckException closeLog() {
        isLog = false;
        return this;
    }
}
