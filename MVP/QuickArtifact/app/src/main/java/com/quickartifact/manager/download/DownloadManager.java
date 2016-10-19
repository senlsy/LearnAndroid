package com.quickartifact.manager.download;

import com.quickartifact.manager.download.task.DownloadFileTask;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

/**
 * Description: 下载管理
 * 1.下载更新文件
 * 2.xx
 *
 * @author liuranchao
 * @date 16/3/21 下午5:49
 */
public final class DownloadManager implements OnChangeListener {

    private static DownloadManager sInstance;

    private OkHttpClient mOkHttpClient;

    private ArrayList<SoftReference<OnChangeListener>> mListenerList;

    /**
     * 单线程池
     */
    private ExecutorService mScheduledThreadPool;

    /**
     * 下载线程
     */
    private DownloadThread mDownloadThread;

    /**
     * 下载的队列
     * String download url
     * DownloadTask 下载对象
     */
    private HashMap<String, DownloadFileTask> mTasks = new HashMap<>();

    //===============================================
    // public
    //===============================================

    private DownloadManager() {
        mListenerList = new ArrayList<>();
        mOkHttpClient = new OkHttpClient.Builder().build();
        mScheduledThreadPool = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取getInstance
     *
     * @return DownloadManager
     */
    public static DownloadManager getInstance() {
        synchronized (DownloadManager.class) {
            if (sInstance == null) {
                sInstance = new DownloadManager();
            }
        }
        return sInstance;
    }

    /**
     * 注册下载监听，页面消失后，要调用反注册
     *
     * @param listener 下载监听
     */
    public void register(OnChangeListener listener) {
        mListenerList.add(new SoftReference(listener));
    }

    /**
     * 反注册下载监听
     *
     * @param listener OnDownloadListener
     */
    public void unregister(OnChangeListener listener) {
        for (int i = 0; i < mListenerList.size(); i++) {
            SoftReference<OnChangeListener> sf = mListenerList.get(i);
            OnChangeListener temp = sf.get();
            if (temp == null || temp == listener) {
                mListenerList.remove(temp);
                i--;
            }
        }
    }

    /**
     * 是否有正在下载中国年的
     *
     * @param url 下载地址
     */
    public boolean isDownloading(String url) {
        return mTasks.containsKey(url);
    }

    /**
     * 添加个下载
     *
     * @param downloadFileTask DownloadTask
     */
    public void addDownloadTask(DownloadFileTask downloadFileTask) {

        if (!isDownloading(downloadFileTask.getUrl())) {
            mTasks.put(downloadFileTask.getUrl(), downloadFileTask);
            mDownloadThread = new DownloadThread(mOkHttpClient, downloadFileTask);
            mDownloadThread.setChangeListener(this);
            mScheduledThreadPool.execute(mDownloadThread);
        }
    }

    /**
     * 在下载完成时，移除下载
     *
     * @param downloadFileTask DownloadTask
     */
    public void remove(DownloadFileTask downloadFileTask) {
        if (downloadFileTask != null) {
            mTasks.remove(downloadFileTask.getUrl());
        }
    }

    /**
     * 取消下载
     *
     * @param key URL
     */
    public void cancel(String key) {
        mTasks.remove(key);
        if (mDownloadThread != null) {
            mDownloadThread.setCancel(true);
        }
    }

    @Override
    public void onDownloadStatusChanged(DownloadFileTask downloadFileTask) {

        for (int i = 0; i < mListenerList.size(); i++) {
            SoftReference<OnChangeListener> sf = mListenerList.get(i);
            OnChangeListener listener = sf.get();
            if (listener != null) {
                listener.onDownloadStatusChanged(downloadFileTask);
            } else {
                mListenerList.remove(sf);
                i--;
            }
        }
    }

    @Override
    public void onDownloadProgressChanged(DownloadFileTask downloadFileTask) {
        for (int i = 0; i < mListenerList.size(); i++) {
            SoftReference<OnChangeListener> sf = mListenerList.get(i);
            OnChangeListener listener = sf.get();
            if (listener != null) {
                listener.onDownloadProgressChanged(downloadFileTask);
            } else {
                mListenerList.remove(sf);
                i--;
            }
        }

    }

    @Override
    public void onDownloadResultChanged(DownloadFileTask downloadFileTask) {
        for (int i = 0; i < mListenerList.size(); i++) {
            SoftReference<OnChangeListener> sf = mListenerList.get(i);
            OnChangeListener listener = sf.get();
            if (listener != null) {
                listener.onDownloadResultChanged(downloadFileTask);
            } else {
                mListenerList.remove(sf);
                i--;
            }
        }
    }

}
