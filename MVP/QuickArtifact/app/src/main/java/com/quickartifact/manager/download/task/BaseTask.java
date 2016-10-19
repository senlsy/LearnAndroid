package com.quickartifact.manager.download.task;

import com.quickartifact.manager.download.config.ResultEnum;
import com.quickartifact.manager.download.config.StatusEnum;
import com.quickartifact.utils.file.DirEnum;

/**
 * Description: task基类
 *
 * @author mark.lin
 * @date 2016/9/26 10:55
 */
public interface BaseTask {


    /**
     * 下载地址
     *
     * @param url
     */
    void setUrl(String url);

    String getUrl();

    /**
     * 存储路径
     *
     * @param path
     */
    void setSaveDir(DirEnum path);

    DirEnum getSaveDir();

    /**
     * 错误异常
     *
     * @param e
     */
    void setExection(Exception e);

    Exception getException();

    /**
     * 下载长度
     *
     * @return
     */
    long getContentLength();

    void setContentLength(long contentLength);

    /**
     * 当前进度
     *
     * @return
     */
    long currentLength();

    void setCurrentLength(long length);

    /**
     * 下载结果
     *
     * @param result
     */
    void setResult(ResultEnum result);

    ResultEnum getResult();

    /**
     * 是否支持断点
     *
     * @return
     */
    boolean isSupportPause();


    void setSupportPause(boolean isSupport);

    /**
     * 断点续传位置
     *
     * @return
     */
    long getStartPosition();


    long setStartPosition(long start);

    /**
     * 当前状态
     *
     * @param status
     */
    void setStatus(StatusEnum status);

    StatusEnum getStatus();

    /**
     * 已存在是否自动重新命名
     *
     * @return
     */
    boolean autoResetName();

    /**
     * 保存的文件名
     *
     * @param fileName
     */
    void setSaveFileName(String fileName);

    String getSaveFileName();


}
