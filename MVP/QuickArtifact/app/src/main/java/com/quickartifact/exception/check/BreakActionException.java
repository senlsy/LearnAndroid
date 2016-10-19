package com.quickartifact.exception.check;

import com.quickartifact.exception.BaseCheckException;

/**
 * Description: 中断异常
 *
 * @author mark.lin
 * @date 2016/9/26 16:01
 */
public class BreakActionException extends BaseCheckException {
    public static final String TAG = "中断异常";
    private String msg;

    public BreakActionException(String msg) {
        this.msg = msg;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + msg;
    }
}
