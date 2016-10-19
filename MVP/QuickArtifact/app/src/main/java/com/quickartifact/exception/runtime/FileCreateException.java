package com.quickartifact.exception.runtime;

import com.quickartifact.exception.BaseRuntimeException;

/**
 * Description: 文件创建异常
 *
 * @author mark.lin
 * @date 2016/9/26 15:51
 */
public class FileCreateException extends BaseRuntimeException {

    public static final String TAG = "文件创建异常";

    private String msg;

    public FileCreateException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
