package com.yuexunit.android.library.library_http.base;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

import com.yuexunit.android.library.library_http.HttpUtils;
import com.yuexunit.android.library.library_http.callback.DefaultHttpRedirectHandler;
import com.yuexunit.android.library.library_http.callback.FileDownloadHandler;
import com.yuexunit.android.library.library_http.callback.HttpRedirectHandler;
import com.yuexunit.android.library.library_http.callback.RequestCallBack;
import com.yuexunit.android.library.library_http.callback.RequestCallBackHandler;
import com.yuexunit.android.library.library_http.callback.StringDownloadHandler;
import com.yuexunit.android.library.library_http.exception.HttpException;
import com.yuexunit.android.library.library_http.util.OtherUtils;
import com.yuexunit.android.library.task.PriorityAsyncTask;

/**
 * @ClassName: HttpHandler
 * @Description: http请求任务类 感谢wyouflf作者提供的开源框架
 * @author LinSQ
 * @date 2015-5-8 下午7:36:53
 * @version
 * @note
 * @param <T>
 */
public class HttpHandler<T> extends PriorityAsyncTask<Object, Object, Void> implements RequestCallBackHandler
{
    
    private final AbstractHttpClient client;
    private final HttpContext context;
    private RequestCallBack<T> callback;
    private String charset; // The default charset of response header info.
    private State state = State.WAITING;// 一开始状态
    private HttpRedirectHandler httpRedirectHandler;// 重定向处理
    private long expiry = HttpCache.getDefaultExpiryTime();// httpResponse缓存有效期
    
    public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        if(httpRedirectHandler != null){
            this.httpRedirectHandler = httpRedirectHandler;
        }
    }
    
    public HttpHandler(AbstractHttpClient client, HttpContext context, String charset, RequestCallBack<T> callback){
        this.client = client;
        this.context = context;
        this.callback = callback;
        this.charset = charset;
        this.client.setRedirectHandler(notUseApacheRedirectHandler);
    }
    
    private String fileSavePath = null;// 保存文件的完正路径
    private boolean isDownloadingFile = false;// 是否是文件下载
    private boolean autoResume = false;// 是否续传
    private boolean autoRename = false;// 是否自动命名
    private HttpRequestBase request;
    private String requestUrl;
    private String requestMethod;
    private boolean isUploading = true;// 上传还是下载？
    private int retriedCount = 0;
    
    @Override
    protected Void doInBackground(Object... params) {
        if(this.state == State.CANCELLED || params == null || params.length == 0)
            return null;
        if(params.length > 3){
            fileSavePath = String.valueOf(params[1]);
            isDownloadingFile = fileSavePath != null;
            autoResume = (Boolean)params[2];
            autoRename = (Boolean)params[3];
        }
        try{
            if(this.state == State.CANCELLED)
                return null;
            request = (HttpRequestBase)params[0];// 任务httpRequest请求
            requestUrl = request.getURI().toString();// 请求url地址
            if(callback != null){
                callback.setRequestUrl(requestUrl);
            }
            this.publishProgress(UPDATE_START);// 开始
            //
            lastUpdateTime = SystemClock.uptimeMillis();
            ResponseInfo<T> responseInfo = sendRequest(request);// 会抛出异常
            if(responseInfo != null){
                this.publishProgress(UPDATE_SUCCESS, responseInfo);// 完成
                return null;
            }
        } catch(HttpException e){
            this.publishProgress(UPDATE_FAILURE, e, e.getMessage());// 失败
        }
        return null;
    }
    
    
    @SuppressWarnings("unchecked")
    private ResponseInfo<T> sendRequest(HttpRequestBase request) throws HttpException {
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while(true){
            if(autoResume && isDownloadingFile){
                File downloadFile = new File(fileSavePath);
                long fileLen = 0;
                if(downloadFile.isFile() && downloadFile.exists()){
                    fileLen = downloadFile.length();
                }
                if(fileLen > 0){
                    request.setHeader("RANGE", "bytes=" + fileLen + "-");
                }
            }
            boolean retry = true;
            IOException exception = null;
            try{
                // 缓存如果存在则直接从缓存取出上次请求的结果。
                requestMethod = request.getMethod();
                if(HttpUtils.sHttpCache.isEnabled(requestMethod)){
                    String result = HttpUtils.sHttpCache.get(requestUrl);
                    if(result != null){
                        return new ResponseInfo<T>(null, (T)result, true);
                    }
                }
                //
                ResponseInfo<T> responseInfo = null;
                if( !isCancelled()){
                    HttpResponse response = client.execute(request, context);// 此处会抛异常
                    responseInfo = handleResponse(response);// 猜出会抛异常
                }
                return responseInfo;
            } catch(UnknownHostException e){
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedCount, context);
            } catch(IOException e){
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedCount, context);
            } catch(NullPointerException e){
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedCount, context);
            } catch(HttpException e){
                throw e;
            } catch(Throwable e){
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedCount, context);
            }
            if( !retry){
                throw new HttpException(exception);
            }
        }
    }
    
    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;
    
    @Override
    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Object... values) {
        if(this.state == State.CANCELLED || values == null || values.length == 0 || callback == null)
            return;
        switch((Integer)values[0]){
            case UPDATE_START:
                this.state = State.STARTED;
                callback.onStart();
                break;
            case UPDATE_LOADING:
                if(values.length != 3)
                    return;
                this.state = State.LOADING;
                callback.onLoading(Long.valueOf(String.valueOf(values[1])),
                                   Long.valueOf(String.valueOf(values[2])),
                                   isUploading);
                break;
            case UPDATE_FAILURE:
                if(values.length != 3)
                    return;
                this.state = State.FAILURE;
                callback.onFailure((HttpException)values[1], (String)values[2]);
                break;
            case UPDATE_SUCCESS:
                if(values.length != 2)
                    return;
                this.state = State.SUCCESS;
                callback.onSuccess((ResponseInfo<T>)values[1]);
                break;
            default:
                break;
        }
    }
    
    @SuppressWarnings("unchecked")
    private ResponseInfo<T> handleResponse(HttpResponse response) throws HttpException, IOException {
        if(response == null){
            throw new HttpException("response is null");
        }
        if(isCancelled())
            return null;
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if(statusCode < 300){
            Object result = null;
            HttpEntity entity = response.getEntity();
            if(entity != null){
                isUploading = false;// 标志接下来的进度回调是下载回调。
                if(isDownloadingFile){
                    autoResume = autoResume && OtherUtils.isSupportRange(response);// 是否支持续传
                    String responseFileName = autoRename ? OtherUtils.getFileNameFromHttpResponse(response) : null;// 从httpResponse中获取文件名
                    FileDownloadHandler fileHandler = new FileDownloadHandler();
                    result = fileHandler.handleEntity(entity, this, fileSavePath, autoResume, responseFileName);
                }
                else{
                    // 字符串
                    StringDownloadHandler strHandler = new StringDownloadHandler();
                    result = strHandler.handleEntity(entity, this, charset);
                    if(HttpUtils.sHttpCache.isEnabled(requestMethod)){
                        // 支持缓存方法的字符串结果会被缓存
                        HttpUtils.sHttpCache.put(requestUrl, (String)result, expiry);
                    }
                }
            }
            return new ResponseInfo<T>(response, (T)result, false);
        }
        else if(statusCode == 301 || statusCode == 302){
            if(httpRedirectHandler == null){
                httpRedirectHandler = new DefaultHttpRedirectHandler();
            }
            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
            if(request != null){
                return this.sendRequest(request);
            }
        }
        else if(statusCode == 416){
            throw new HttpException(statusCode, "maybe the file has downloaded completely");
        }
        else{
            throw new HttpException(statusCode, status.getReasonPhrase());
        }
        return null;
    }
    
    /**
     * cancel request task.
     */
    @Override
    public void cancel() {
        this.state = State.CANCELLED;
        if(request != null && !request.isAborted()){
            try{
                request.abort();
            } catch(Throwable e){
            }
        }
        if( !this.isCancelled()){
            try{
                this.cancel(true);
            } catch(Throwable e){
            }
        }
        if(callback != null){
            callback.onCancelled();
        }
    }
    
    private long lastUpdateTime;
    
    // RequestCallBackHandler接口的实现，上传进度的回调
    @Override
    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {
        if(callback != null && this.state != State.CANCELLED){
            if(forceUpdateUI){
                this.publishProgress(UPDATE_LOADING, total, current);
            }
            else{
                long currTime = SystemClock.uptimeMillis();
                if(currTime - lastUpdateTime >= callback.getRate()){
                    lastUpdateTime = currTime;
                    this.publishProgress(UPDATE_LOADING, total, current);
                }
            }
        }
        return this.state != State.CANCELLED;
    }
    
    public void setRequestCallBack(RequestCallBack<T> callback) {
        this.callback = callback;
    }
    
    public RequestCallBack<T> getRequestCallBack() {
        return this.callback;
    }
    
    public State getState() {
        return state;
    }
    
    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }
    
    public enum State {
        WAITING(0), STARTED(1), LOADING(2), FAILURE(3), CANCELLED(4), SUCCESS(5);
        
        private int value = 0;
        
        State(int value){
            this.value = value;
        }
        
        public static State valueOf(int value) {
            switch(value){
                case 0:
                    return WAITING;
                case 1:
                    return STARTED;
                case 2:
                    return LOADING;
                case 3:
                    return FAILURE;
                case 4:
                    return CANCELLED;
                case 5:
                    return SUCCESS;
                default:
                    return FAILURE;
            }
        }
        
        public int value() {
            return this.value;
        }
    }
    
    private static final NotUseApacheRedirectHandler notUseApacheRedirectHandler = new NotUseApacheRedirectHandler();
    
    private static final class NotUseApacheRedirectHandler implements RedirectHandler
    {
        
        @Override
        public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
            return false;
        }
        
        @Override
        public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
            return null;
        }
    }
}
