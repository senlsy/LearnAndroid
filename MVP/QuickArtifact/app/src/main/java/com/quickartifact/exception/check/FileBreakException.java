package com.quickartifact.exception.check;

import com.quickartifact.exception.BaseCheckException;

/**
 * Description: 文件损坏异常
 *
 * @author mark.lin
 * @date 2016/9/14 11:25
 */
public class FileBreakException extends BaseCheckException {

    public static final String TAG = "文件损坏异常";
    private String mFile;

    public FileBreakException(String file) {
        mFile = file;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + mFile;
    }


}
