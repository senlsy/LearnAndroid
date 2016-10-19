package com.quickartifact.exception.runtime;

import com.quickartifact.exception.BaseRuntimeException;

/**
 * 类描述：加密异常
 * 创建人：mark.lin
 * 创建时间：2016/9/28 10:42
 * 修改备注：
 */
public class SignatureException extends BaseRuntimeException {
    public static final String TAG = "加密异常";

    private String msg;

    public SignatureException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
