package com.quickartifact.utils.log;

import android.os.SystemClock;

import com.quickartifact.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Description: 日志记录器,可以作为跟踪对象的成员变量，记录该对象的操作
 *
 * @author mark.lin
 * @date 2016/9/6 15:44
 */
public class MarkerLog implements Serializable {


    /**
     * 日志开关
     */
    public static final boolean ENABLE = BuildConfig.DEBUG;

    /**
     * 超时level提升为WARM
     */
    public static final int WARMMING_TIME = 3 * 1000;

    /**
     * mraker容器
     */
    private List<Marker> mMarkers = new ArrayList<>();

    /**
     * 结束标识
     */
    private boolean mIsFinished = false;

    /**
     * 提示等级
     */
    private LogLevelEnum mLevel;


    public MarkerLog() {
        mLevel = LogLevelEnum.INFO;
        if (ENABLE) {
            mMarkers = new ArrayList<Marker>();
        }
    }


    /**
     * 添加记录
     *
     * @param msg
     */
    public synchronized void add(String msg) {
        if (!ENABLE || mIsFinished) {
            return;
        }
        mMarkers.add(new Marker(msg));
    }

    /**
     * 记录结束，输入所有记录日志
     *
     * @param header
     */
    public synchronized void finish(String header) {
        if (!ENABLE || mIsFinished || mMarkers.isEmpty()) {
            return;
        }

        mIsFinished = true;

        StringBuilder sb = new StringBuilder();
        long duration = getTotalDuration();
        sb.append(header + ">>>>>>>>duration: " + duration + "\n");

        long prevTime = mMarkers.get(0).mTime;
        for (Marker marker : mMarkers) {
            long thisTime = marker.mTime;
            String item = String.format(Locale.US, "+%-4d(ms),[%s]: %s", (thisTime - prevTime), marker.mThreadId, marker.mMsg);
            sb.append(item + "\n");
            prevTime = thisTime;
        }
        mMarkers.clear();

        if (mLevel.getLevel() < LogLevelEnum.WARM.getLevel() && duration > WARMMING_TIME) {
            LogUtils.w(sb.toString());
            return;
        }

        switch (mLevel) {
            case VERBOSE:
                LogUtils.v(sb.toString());
                break;
            case DEBUG:
                LogUtils.d(sb.toString());
                break;
            case INFO:
                LogUtils.i(sb.toString());
                break;
            case WARM:
                LogUtils.w(sb.toString());
                break;
            case ERROR:
                LogUtils.e(sb.toString());
                break;
            default:
                break;
        }
    }

    //====================================================================
    @Override
    protected void finalize() throws Throwable {
        if (!mIsFinished) {
            finish("Have MarkerLog obj forget call MarkerLog.finish()");
        }
        super.finalize();
    }

    private long getTotalDuration() {
        long first = mMarkers.get(0).mTime;
        long last = mMarkers.get(mMarkers.size() - 1).mTime;
        return last - first;
    }

    private static class Marker implements Serializable {
        public final String mMsg;
        public final String mThreadId;
        public final long mTime;

        public Marker(String msg) {
            this.mMsg = msg;
            this.mThreadId = Thread.currentThread().getName() + "_" + Thread.currentThread().getName();
            mTime = SystemClock.elapsedRealtime();
        }
    }

    public class Builder {
        public void createVerbose() {
            MarkerLog.this.mLevel = LogLevelEnum.VERBOSE;
        }

        public void createInfo() {
            MarkerLog.this.mLevel = LogLevelEnum.INFO;
        }

        public void createDebug() {
            MarkerLog.this.mLevel = LogLevelEnum.DEBUG;
        }

        public void createWarm() {
            MarkerLog.this.mLevel = LogLevelEnum.WARM;
        }

        public void createError() {
            MarkerLog.this.mLevel = LogLevelEnum.ERROR;
        }
    }


}
