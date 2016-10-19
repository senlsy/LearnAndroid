package com.quickartifact.manager.download.task;


import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.manager.download.config.ResultEnum;
import com.quickartifact.manager.download.config.StatusEnum;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.DirEnum;

import java.io.File;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/9/26 17:53
 * 修改备注：
 */
public class DownloadFileTask implements BaseTask {


    private String mUrl;
    private DirEnum mSaveDir;
    private Exception mException;
    private long mContentLength;
    private long mCurrentLength;
    private ResultEnum mResult;
    private boolean isSupprotPause;
    private StatusEnum mStatus;
    private boolean isAutoResetName;

    private String mSaveFileName;
    private long mStartPostition;

    @Override
    public void setUrl(String url) {
        mUrl = url;

    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public void setSaveDir(DirEnum path) {
        mSaveDir = path;
    }

    @Override
    public DirEnum getSaveDir() {
        return mSaveDir;
    }

    @Override
    public void setExection(Exception e) {
        mException = e;
    }

    @Override
    public Exception getException() {
        return mException;
    }

    @Override
    public long getContentLength() {
        return mContentLength;
    }

    @Override
    public void setContentLength(long totalSize) {
        mContentLength = totalSize;
    }

    @Override
    public long currentLength() {
        return mCurrentLength;
    }

    @Override
    public void setCurrentLength(long length) {
        mCurrentLength = length;
    }

    @Override
    public void setResult(ResultEnum result) {
        mResult = result;
    }

    @Override
    public ResultEnum getResult() {
        return mResult;
    }

    @Override
    public boolean isSupportPause() {
        return isSupprotPause;
    }

    @Override
    public void setSupportPause(boolean isSupport) {
        isSupprotPause = isSupport;
    }

    @Override
    public long getStartPosition() {
        return mStartPostition;
    }

    @Override
    public long setStartPosition(long start) {
        return mStartPostition = start;
    }

    @Override
    public void setStatus(StatusEnum status) {
        mStatus = status;
    }

    @Override
    public StatusEnum getStatus() {
        return mStatus;
    }

    @Override
    public boolean autoResetName() {
        return isAutoResetName;
    }

    @Override
    public void setSaveFileName(String fileName) {
        mSaveFileName = fileName;
    }

    /**
     * 若没有指定保存的文件名，则会根据url解析出文件名，所以必须先配置了url。
     * 若无法自动解析出文件名，则以temp_当前时间.qijia作为保存的文件名
     */
    @Override
    public String getSaveFileName() {

        if (CheckUtils.checkStrHasEmpty(mSaveFileName)) {
            mSaveFileName = FileUtils.decodeFileName(getUrl());
        }

        //对于已下载的文件是否进行重命名再次下载。
        if (isAutoResetName) {
            String newName = mSaveFileName;
            int count = 1;
            File saveFile = FileUtils.getFile(mSaveDir, mSaveFileName);
            while (CheckUtils.checkFileExists(saveFile)) {
                newName = mSaveFileName.substring(0, mSaveFileName.lastIndexOf(".")) +
                        "(" + count++ + ")" +
                        mSaveFileName.substring(mSaveFileName.lastIndexOf("."));
                saveFile = FileUtils.getFile(mSaveDir, newName);
            }
            mSaveFileName = newName;
        }
        return mSaveFileName;
    }


}
