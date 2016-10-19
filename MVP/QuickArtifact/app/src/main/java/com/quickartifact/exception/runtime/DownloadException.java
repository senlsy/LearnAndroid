package com.quickartifact.exception.runtime;

import com.quickartifact.exception.BaseRuntimeException;

/**
 * Description: TODO
 *
 * @author mark.lin
 * @date 2016/9/20 16:12
 */
public class DownloadException extends BaseRuntimeException {

    public final static String TAG = "下载异常";
    private String mUrl;

    public DownloadException(String fileName) {
        this.mUrl = fileName;
    }

    @Override
    protected String getTag() {
        return TAG + ":" + mUrl;
    }
}
