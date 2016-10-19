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
public abstract class BaseRuntimeException extends RuntimeException {

    private boolean isLog = true;

    protected abstract String getTag();

    public BaseRuntimeException() {
    }

    public BaseRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public BaseRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BaseRuntimeException(Throwable throwable) {
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

    public BaseRuntimeException closeLog() {
        isLog = false;
        return this;
    }
}
