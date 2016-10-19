package com.quickartifact.exception.check;

import com.quickartifact.exception.BaseCheckException;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/8 11:25
 * 修改备注：
 */
public class UnKnowException extends BaseCheckException {
    public static final String TAG = "无法识别";
    private String msg;

    public UnKnowException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
