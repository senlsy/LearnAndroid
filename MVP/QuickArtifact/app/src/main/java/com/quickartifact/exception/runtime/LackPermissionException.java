package com.quickartifact.exception.runtime;

import com.quickartifact.exception.BaseRuntimeException;

/**
 * 类描述：权限缺失异常
 * 创建人：mark.lin
 * 创建时间：2016/9/28 16:55
 * 修改备注：
 */
public class LackPermissionException extends BaseRuntimeException {
    public static final String TAG = "缺失权限异常";

    private String msg;

    public LackPermissionException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
