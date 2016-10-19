package com.yuexunit.android.library.task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @ClassName: PriorityExecutor
 * @Description: 优先级执行者Executor 感谢作者wyouflf提供的开源框架
 * @author LinSQ
 * @date 2015-4-20 下午10:41:36
 * @version
 * @note
 */
public class PriorityExecutor implements Executor
{
    
    private static final int CORE_POOL_SIZE=5;
    private static final int MAXIMUM_POOL_SIZE=256;
    private static final int KEEP_ALIVE=1;
    //
    private static final ThreadFactory sThreadFactory=new ThreadFactory(){
        
        // 线程工厂
        private final AtomicInteger mCount=new AtomicInteger(1);
        
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "PriorityExecutor(优先级执行者) #" + mCount.getAndIncrement());
        }
    };
    
    
    private final BlockingQueue<Runnable> mPoolWorkQueue=new PriorityObjectBlockingQueue<Runnable>();// 自定义优先级队列
    private final ThreadPoolExecutor mThreadPoolExecutor;
    
    public PriorityExecutor(){
        this(CORE_POOL_SIZE);
    }
    
    public PriorityExecutor(int poolSize){
        mThreadPoolExecutor=new ThreadPoolExecutor(poolSize,
                                                   MAXIMUM_POOL_SIZE,
                                                   KEEP_ALIVE,
                                                   TimeUnit.SECONDS,
                                                   mPoolWorkQueue,
                                                   sThreadFactory);
    }
    
    public int getPoolSize() {
        return mThreadPoolExecutor.getCorePoolSize();
    }
    
    public void setPoolSize(int poolSize) {
        if(poolSize > 0){
            mThreadPoolExecutor.setCorePoolSize(poolSize);
        }
    }
    
    public boolean isBusy() {
        return mThreadPoolExecutor.getActiveCount() >= mThreadPoolExecutor.getCorePoolSize();
    }
    
    @Override
    public void execute(final Runnable r) {
        mThreadPoolExecutor.execute(r);
    }
}
