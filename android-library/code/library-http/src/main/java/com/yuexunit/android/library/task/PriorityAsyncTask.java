/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuexunit.android.library.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yuexunit.android.library.library_utils.log.Logger;


/**
 * Author: wyouflf Date: 14-5-23 Time: 上午11:25
 */
public abstract class PriorityAsyncTask<Params, Progress, Result> implements TaskHandler
{
    
    private static final int MESSAGE_POST_RESULT=0x1;
    private static final int MESSAGE_POST_PROGRESS=0x2;
    private static final InternalHandler sHandler=new InternalHandler();
    public static final Executor sDefaultExecutor=new PriorityExecutor();// 自定义执行者（具备优先级）
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;
    private volatile boolean mExecuteInvoked=false;// 是否已执行过
    private final AtomicBoolean mCancelled=new AtomicBoolean();// 是否取消
    private final AtomicBoolean mTaskInvoked=new AtomicBoolean();// 任务是否在启动了
    private Priority priority;// 任务执行优先级
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority=priority;
    }
    
    /**
     * Creates a new asynchronous task. This constructor must be invoked on the
     * UI thread.
     */
    public PriorityAsyncTask(){
        mWorker=new WorkerRunnable<Params, Result>(){
            
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                return postResult(doInBackground(mParams));
            }
        };
        // 和AFinal一样，通过FutureTask来监听mWorker的（运行结束、cancle、exception）
        mFuture=new FutureTask<Result>(mWorker){
            
            @Override
            protected void done() {
                try{
                    // postResultIfNotInvoked：如果task在还没运行起来的时候cancle，会发送未运行的结果。
                    // 但是task一旦运行起来，就不会发送结果了，wasTaskInvoked=true了。
                    // task正常运行的结果，会在mWorker的call()方法中发送了。
                    postResultIfNotInvoked(get());
                } catch(InterruptedException e){
                    Logger.w(e, false);
                } catch(ExecutionException e){
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                } catch(CancellationException e){
                    postResultIfNotInvoked(null);
                }
            }
        };
    }
    
    // 回调函数--------------------------
    /**
     * @Description:task进入done状态的回调。
     * @param
     * @return void
     * @note
     */
    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked=mTaskInvoked.get();
        if( !wasTaskInvoked){
            // task启动后，进入done状态不会发送结果到主线程上的回调。
            postResult(result);
        }
    }
    
    // task发送结果（异步线程上），回调到finish()主线程上执行。
    private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message=sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }
    
    /**
     * Runs on the UI thread before {@link #doInBackground}.回调运行在主线程上。
     * 
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
        // 在运行前，有主线程直接调用
    }
    
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute} by the
     * caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates on the
     * UI thread.
     * 
     * @param params
     *            The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);
    
    /**
     * This method can be invoked from {@link #doInBackground} to publish
     * updates on the UI thread while the background computation is still
     * running. Each call to this method will trigger the execution of
     * {@link #onProgressUpdate} on the UI thread.
     * <p/>
     * {@link #onProgressUpdate} will note be called if the task has been
     * canceled.
     * 
     * @param values
     *            The progress values to update the UI with.
     * @see #onProgressUpdate
     * @see #doInBackground
     */
    // 发送更新进度（异步线程上执行），回调onProgressUpdate（主线程上执行）
    protected final void publishProgress(Progress... values) {
        if( !isCancelled()){
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }
    
    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked. The
     * specified values are the values passed to {@link #publishProgress}.
     * 
     * @param values
     *            The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onProgressUpdate(Progress... values) {
        // 由handler发起的更新进度的回调，运行在主线程上。
    }
    
    private void finish(Result result) {
        // task运行结束，由handler发起的回调，运行在主线程上。
        if(isCancelled()){
            onCancelled(result);
        }
        else{
            onPostExecute(result);
        }
    }
    
    /**
     * <p>
     * Runs on the UI thread after {@link #doInBackground}. The specified result
     * is the value returned by {@link #doInBackground}.
     * </p>
     * <p/>
     * <p>
     * This method won't be invoked if the task was cancelled.
     * </p>
     * 
     * @param result
     *            The result of the operation computed by
     *            {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onPostExecute(Result result) {
        // finish上的调用。提交结果，主线程上执行
    }
    
    /**
     * <p>
     * Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.
     * </p>
     * <p/>
     * <p>
     * The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.
     * </p>
     * 
     * @param result
     *            The result, if any, computed in
     *            {@link #doInBackground(Object[])}, can be null
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void onCancelled(Result result) {
        // finish上的调用。cancle的回调，主线程上执行
        onCancelled();
    }
    
    /**
     * <p>
     * Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.
     * </p>
     * <p/>
     * <p>
     * Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.
     * </p>
     * 
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }
    
    // -----------------------------------------------------
    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally. If you are calling {@link #cancel(boolean)} on the task, the
     * value returned by this method should be checked periodically from
     * {@link #doInBackground(Object[])} to end the task as soon as possible.
     * 
     * @return <tt>true</tt> if task was cancelled before it completed
     * @see #cancel(boolean)
     */
    @Override
    public final boolean isCancelled() {
        return mCancelled.get();
    }
    
    /**
     * @param mayInterruptIfRunning
     *            <tt>true</tt> if the thread executing this task should be
     *            interrupted; otherwise, in-progress tasks are allowed to
     *            complete.
     * @return <tt>false</tt> if the task could not be cancelled, typically
     *         because it has already completed normally; <tt>true</tt>
     *         otherwise
     * @see #isCancelled()
     * @see #onCancelled(Object)
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }
    
    // TaskHandler的实现-------------------------------
    @Override
    public boolean supportPause() {
        return false;
    }
    
    @Override
    public boolean supportResume() {
        return false;
    }
    
    @Override
    public boolean supportCancel() {
        return true;
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void cancel() {
        this.cancel(true);
    }
    
    @Override
    public boolean isPaused() {
        return false;
    }
    
    // ----------------------------
    /**
     * Waits if necessary for the computation to complete, and then retrieves
     * its result.阻塞
     * 
     * @return The computed result.
     * @throws java.util.concurrent.CancellationException
     *             If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException
     *             If the computation threw an exception.
     * @throws InterruptedException
     *             If the current thread was interrupted while waiting.
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }
    
    /**
     * Waits if necessary for at most the given time for the computation to
     * complete, and then retrieves its result.阻塞
     * 
     * @param timeout
     *            Time to wait before cancelling the operation.
     * @param unit
     *            The time unit for the timeout.
     * @return The computed result.
     * @throws java.util.concurrent.CancellationException
     *             If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException
     *             If the computation threw an exception.
     * @throws InterruptedException
     *             If the current thread was interrupted while waiting.
     * @throws java.util.concurrent.TimeoutException
     *             If the wait timed out.
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }
    
    // --------------------------
    /**
     * @param params
     *            The parameters of the task.
     * @return This instance of AsyncTask.
     * @throws IllegalStateException
     *             If execute has invoked.
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     * @see #execute(Runnable)
     */
    public final PriorityAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }
    
    /**
     * @param exec
     *            The executor to use.
     * @param params
     *            The parameters of the task.
     * @return This instance of AsyncTask.
     * @throws IllegalStateException
     *             If execute has invoked.
     * @see #execute(Object[])
     */
    public final PriorityAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if(mExecuteInvoked){
            throw new IllegalStateException("Cannot execute task:the task is already executed.");
        }
        mExecuteInvoked=true;
        onPreExecute();
        mWorker.mParams=params;// 设置了任务所需的参数
        exec.execute(new PriorityRunnable(priority, mFuture));
        return this;
    }
    
    /**
     * Convenience version of {@link #execute(Object...)} for use with a simple
     * Runnable object. See {@link #execute(Object[])} for more information on
     * the order of execution.
     * 
     * @see #execute(Object[])
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     */
    public static void execute(Runnable runnable) {
        execute(runnable, Priority.DEFAULT);
    }
    
    /**
     * Convenience version of {@link #execute(Object...)} for use with a simple
     * Runnable object. See {@link #execute(Object[])} for more information on
     * the order of execution.
     * 
     * @see #execute(Object[])
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     */
    public static void execute(Runnable runnable, Priority priority) {
        sDefaultExecutor.execute(new PriorityRunnable(priority, runnable));
    }
    
    // -----------------------------------
    private static class InternalHandler extends Handler
    {
        
        private InternalHandler(){
            super(Looper.getMainLooper());
        }
        
        @SuppressWarnings({"unchecked","RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result=(AsyncTaskResult<?>)msg.obj;
            switch(msg.what){
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }
    
    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result>
    {
        
        Params[] mParams;
    }
    
    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class AsyncTaskResult<Data>
    {
        
        final PriorityAsyncTask mTask;
        final Data[] mData;
        
        AsyncTaskResult(PriorityAsyncTask task, Data... data){
            mTask=task;
            mData=data;
        }
    }
}
