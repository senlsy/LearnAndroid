package com.yuexunit.android.library.library_http.callback;

/**
 * @ClassName: RequestCallBackHandler
 * @Description: 进度更新的回调接口
 * @author LinSQ
 * @date 2015-5-11 上午9:34:05
 * @version
 * @note
 */
public interface RequestCallBackHandler
{
    
    /**
     * @param total
     * @param current
     * @param forceUpdateUI
     * @return continue
     */
    boolean updateProgress(long total, long current, boolean forceUpdateUI);
}
