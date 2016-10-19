package com.quickartifact.manager.download;


import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.manager.download.config.DownloadConfig;
import com.quickartifact.manager.download.config.ResultEnum;
import com.quickartifact.manager.download.config.StatusEnum;
import com.quickartifact.manager.download.task.DownloadFileTask;
import com.quickartifact.exception.check.BreakActionException;
import com.quickartifact.exception.check.FileBreakException;
import com.quickartifact.exception.runtime.FileCreateException;
import com.quickartifact.exception.runtime.InitializationException;
import com.quickartifact.utils.file.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description: 下载的线程
 *
 * @author liuranchao
 * @date 16/3/22 上午9:24
 */
public class DownloadThread implements Runnable {


    /**
     * 是否取消下载
     */
    private boolean mIsCancel;

    private OkHttpClient mOkHttpClient;

    private DownloadFileTask mTask;

    private OnChangeListener mChangeListener;

    public void setCancel(boolean cancel) {
        mIsCancel = cancel;
    }

    public DownloadThread(OkHttpClient okHttpClient, DownloadFileTask downloadFileTask) {
        mOkHttpClient = okHttpClient;
        mTask = downloadFileTask;
    }

    public void setChangeListener(OnChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    @Override
    public void run() {

        try {

            if (mChangeListener == null) {
                throw new InitializationException("Must set OnDownloadProgressListener ");
            }

            File downloadFile = FileUtils.createNewFile(mTask.getSaveDir(), mTask.getSaveFileName());
            if (CheckUtils.checkFileExists(downloadFile)) {
                throw new FileCreateException(mTask.getUrl());
            }


            Response response = mOkHttpClient.newCall(createRequest(mTask.getUrl())).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }


            long totalLength = response.body().contentLength();
            mTask.setContentLength(totalLength);
            mTask.setStatus(StatusEnum.DOWNLOAD_STATUS_BEGIN);
            mChangeListener.onDownloadStatusChanged(mTask);

            BufferedInputStream bis = new BufferedInputStream(response.body().byteStream(), DownloadConfig.BUFFER_SIZE_READER);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(downloadFile), DownloadConfig.BUFFER_SIZE_WRITE);

            long currentLength = 0;
            byte[] bs = new byte[DownloadConfig.BUFFER_SIZE_READER];
            int len;
            while ((len = bis.read(bs)) != -1) {

                if (mIsCancel) {
                    mTask.setExection(new BreakActionException(mTask.getUrl()));
                    downloadFailed();
                    downloadEnd();
                    break;
                }

                bos.write(bs, 0, len);
                currentLength += len;

                // 设置下载正在进行的状态监听
                if (mTask.getStatus() != StatusEnum.DOWNLOAD_STATUS_GOING) {
                    mTask.setStatus(StatusEnum.DOWNLOAD_STATUS_GOING);
                    mChangeListener.onDownloadStatusChanged(mTask);
                }

                // 设置下载进度的监听
                mTask.setCurrentLength(currentLength);
                mChangeListener.onDownloadProgressChanged(mTask);
            }

            bos.flush();
            bos.close();
            bis.close();

            if (totalLength != downloadFile.length()) {
                throw new FileBreakException(mTask.getUrl() + "-->" + downloadFile.getAbsolutePath());
            }

            // 设置下载正在进行的状态监听
            mTask.setResult(ResultEnum.DOWNLOAD_RESULT_SUCCESS);
            mChangeListener.onDownloadResultChanged(mTask);

        } catch (IOException e) {
            e.printStackTrace();
            mTask.setExection(e);
            downloadFailed();
        } catch (Exception e) {
            e.printStackTrace();
            mTask.setExection(e);
            downloadFailed();
        }
        downloadEnd();

    }

    /**
     * 下载失败
     */
    private void downloadFailed() {
        FileUtils.delete(FileUtils.getFile(mTask.getSaveDir(), mTask.getSaveFileName()));
        mTask.setResult(ResultEnum.DOWNLOAD_RESULT_FAILURE);
        mChangeListener.onDownloadResultChanged(mTask);
    }

    /**
     * 下载完成
     */
    private void downloadEnd() {
        // 设置下载正在进行的状态监听
        mTask.setStatus(StatusEnum.DOWNLOAD_STATUS_END);
        mChangeListener.onDownloadStatusChanged(mTask);
    }

    /**
     * 创建下载的Request
     *
     * @param url 下载地址
     * @return Request
     */
    private Request createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }


}
