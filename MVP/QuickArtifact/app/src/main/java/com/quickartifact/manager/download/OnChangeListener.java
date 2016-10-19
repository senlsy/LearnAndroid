package com.quickartifact.manager.download;

import android.support.annotation.WorkerThread;

import com.quickartifact.manager.download.task.DownloadFileTask;

/**
 * Description: 下载监听器
 *
 * @author liuranchao
 * @date 16/3/21 下午6:38
 */
public interface OnChangeListener {

    /**
     * 下载状态变化的监听
     *
     * @param downloadFileTask 下载任务
     */
    @WorkerThread
    void onDownloadStatusChanged(DownloadFileTask downloadFileTask);

    /**
     * 下载进度变化的监听
     *
     * @param downloadFileTask 下载任务
     */
    @WorkerThread
    void onDownloadProgressChanged(DownloadFileTask downloadFileTask);

    /**
     * 下载结果变化的监听
     *
     * @param downloadFileTask 下载任务
     */
    @WorkerThread
    void onDownloadResultChanged(DownloadFileTask downloadFileTask);
}
