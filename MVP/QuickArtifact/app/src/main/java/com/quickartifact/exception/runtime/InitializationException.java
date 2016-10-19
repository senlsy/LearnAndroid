package com.quickartifact.exception.runtime;

import com.quickartifact.exception.BaseRuntimeException;

/**
 * Description: 初始化异常
 *
 * @author mark.lin
 * @date 2016/9/26 11:34
 */
public class InitializationException extends BaseRuntimeException {
    public static final String TAG = "初始化异常";

    private String msg;

    public InitializationException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
