package com.yuexunit.android.library.library_http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;

import com.yuexunit.android.library.library_http.base.HttpCache;
import com.yuexunit.android.library.library_http.base.HttpHandler;
import com.yuexunit.android.library.library_http.base.RequestParams;
import com.yuexunit.android.library.library_http.base.ResponseStream;
import com.yuexunit.android.library.library_http.base.SyncHttpHandler;
import com.yuexunit.android.library.library_http.callback.HttpRedirectHandler;
import com.yuexunit.android.library.library_http.callback.RequestCallBack;
import com.yuexunit.android.library.library_http.client.DefaultSSLSocketFactory;
import com.yuexunit.android.library.library_http.client.HttpRequest;
import com.yuexunit.android.library.library_http.client.RetryHandler;
import com.yuexunit.android.library.library_http.client.entity.GZipDecompressingEntity;
import com.yuexunit.android.library.library_http.exception.HttpException;
import com.yuexunit.android.library.library_http.util.OtherUtils;
import com.yuexunit.android.library.task.PriorityExecutor;


/**
 * @ClassName: HttpUtils
 * @Description: http请求，感谢作者wyouflf提供的开源框架
 * @author LinSQ
 * @date 2015-4-20 下午10:03:13
 * @version
 * @note
 */
public class HttpUtils
{
    
    // ************************************ default settings & fields
    private final static int DEFAULT_SOCKET_BUFFERSIZE=1024 * 8;
    private final static int DEFAULT_HOST_CONNECTIONS=10;
    private final static int DEFAULT_MAX_CONNECTIONS=10;
    //
    private final static int DEFAULT_CONNECTION_TIMEOUT=1000 * 10;
    private final static int DEFAULT_TIMEOUT=1000 * 10;
    private final static int DEFAULT_SO_TIMEOUT=1000 * 10;
    //
    private final static int DEFAULT_CONN_TIMEOUT=1000 * 10;
    private final static int DEFAULT_RETRY_TIMES=3;
    private final static String HEADER_ACCEPT_ENCODING="Accept-Encoding";
    private final static String ENCODING_GZIP="gzip";
    //
    private final static int DEFAULT_POOL_SIZE=5;
    private final static PriorityExecutor EXECUTOR=new PriorityExecutor(DEFAULT_POOL_SIZE);// 优先级执行者
    //
    public final static HttpCache sHttpCache=new HttpCache();// http响应缓存
    private String responseTextCharset=HTTP.UTF_8;
    private long currentRequestExpiry=HttpCache.getDefaultExpiryTime();// httpResponse缓存的有效期
    //
    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext=new BasicHttpContext();
    private HttpRedirectHandler httpRedirectHandler;// 重定向处理
    
    public HttpUtils(){
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, null);
    }
    
    public HttpUtils(int connTimeout){
        this(connTimeout, null);
    }
    
    public HttpUtils(String userAgent){
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, userAgent);
    }
    
    public HttpUtils(int connTimeout, String userAgent){
        HttpParams params=new BasicHttpParams();
        //
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));// 每台主机最大连接
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONNECTIONS);// 客户端最大连接数
        ConnManagerParams.setTimeout(params, DEFAULT_TIMEOUT);// 连接池取连接超时
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SO_TIMEOUT);// 响应超时
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_CONNECTION_TIMEOUT);// 网络连接超时
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFERSIZE);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(params, true);
        if(TextUtils.isEmpty(userAgent)){
            userAgent=OtherUtils.getUserAgent(null);
        }
        HttpProtocolParams.setUserAgent(params, userAgent);
        SchemeRegistry schemeRegistry=new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));
        //
        httpClient=new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
        httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));// 请求重试
        httpClient.addRequestInterceptor(new HttpRequestInterceptor(){
            
            // 请求拦截器
            @Override
            public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                if( !httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)){
                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });
        httpClient.addResponseInterceptor(new HttpResponseInterceptor(){
            
            // 响应拦截器
            @Override
            public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                final HttpEntity entity=response.getEntity();
                if(entity == null){
                    return;
                }
                final Header encoding=entity.getContentEncoding();
                if(encoding != null){
                    for(HeaderElement element:encoding.getElements()){
                        if(element.getName().equalsIgnoreCase("gzip")){
                            response.setEntity(new GZipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }
    
    /**
     * @Description: 获取httpClient连接实例， private final常量
     * @param
     * @return HttpClient
     * @note
     */
    public HttpClient getHttpClient() {
        return this.httpClient;
    }
    
    /**
     * @Description: 设置响应编码
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configResponseTextCharset(String charSet) {
        if( !TextUtils.isEmpty(charSet)){
            this.responseTextCharset=charSet;
        }
        return this;
    }
    
    /**
     * @Description: 设置重定向处理
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        this.httpRedirectHandler=httpRedirectHandler;
        return this;
    }
    
    /**
     * @Description: 设置响应缓存大小
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configHttpCacheSize(int httpCacheSize) {
        sHttpCache.setCacheSize(httpCacheSize);
        return this;
    }
    
    /**
     * @Description: 设置响应缓存默认有效期
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configDefaultHttpCacheExpiry(long defaultExpiry) {
        HttpCache.setDefaultExpiryTime(defaultExpiry);
        currentRequestExpiry=HttpCache.getDefaultExpiryTime();
        return this;
    }
    
    /**
     * @Description: 设置响应缓存有效期
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configCurrentHttpCacheExpiry(long currRequestExpiry) {
        this.currentRequestExpiry=currRequestExpiry;
        return this;
    }
    
    /**
     * @Description: 设置cookie存储介质
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return this;
    }
    
    /**
     * @Description: 设置客户端信息
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
        return this;
    }
    
    /**
     * @Description: 设置连接超时时间
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configTimeout(int timeout) {
        final HttpParams httpParams=this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        return this;
    }
    
    /**
     * @Description: 设置响应超时时间
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configSoTimeout(int timeout) {
        final HttpParams httpParams=this.httpClient.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        return this;
    }
    
    /**
     * @Description: 注册支持的协议
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configRegisterScheme(Scheme scheme) {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
    }
    
    public HttpUtils configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        Scheme scheme=new Scheme("https", sslSocketFactory, 443);
        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
    }
    
    /**
     * @Description: 设置重试请求次数
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configRequestRetryCount(int count) {
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
        return this;
    }
    
    /**
     * @Description: 设置线程池大小
     * @param
     * @return HttpUtils
     * @note
     */
    public HttpUtils configRequestThreadPoolSize(int threadPoolSize) {
        HttpUtils.EXECUTOR.setPoolSize(threadPoolSize);
        return this;
    }
    
    // ***************************************** send request
    // *******************************************
    /**
     * @Title: send
     * @Description:在异步线程上发送一个http请求
     * @param method
     *            http请求方式
     * @param url
     *            服务器url地址
     * @param callBack
     *            http响应回调对象
     * @return
     */
    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestCallBack<T> callBack) {
        return send(method, url, null, callBack);
    }
    
    /**
     * @Title: send
     * @Description:在异步线程上发送一个http请求
     * @param method
     *            http请求方式
     * @param url
     *            服务器url地址
     * @param params
     *            http请求参数
     * @param callBack
     *            http响应回调对象
     * @return
     */
    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestParams params, RequestCallBack<T> callBack) {
        if(url == null)
            throw new IllegalArgumentException("url may not be null");
        HttpRequest request=new HttpRequest(method, url);// 构建最基本的HttpRequest
        // -------------------------------------
        return sendRequest(request, params, callBack);
    }
    
    /**
     * @Title: send
     * @Description:在同一个线程上发送一个http请求
     * @param method
     *            http请求方式
     * @param url
     *            服务器url地址
     * 
     * @return
     */
    public ResponseStream sendSync(HttpRequest.HttpMethod method, String url) throws HttpException {
        return sendSync(method, url, null);
    }
    
    /**
     * @Title: send
     * @Description:在同一个线程上发送一个http请求
     * @param method
     *            http请求方式
     * @param url
     *            服务器url地址
     * @param params
     *            http请求参数
     * @return
     */
    public ResponseStream sendSync(HttpRequest.HttpMethod method, String url, RequestParams params) throws HttpException {
        if(url == null)
            throw new IllegalArgumentException("url may not be null");
        HttpRequest request=new HttpRequest(method, url);// 构建最基本的HttpRequest
        // -------------------------------------
        return sendSyncRequest(request, params);
    }
    
    // ***************************************** download
    // *******************************************
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, null, false, false, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param autoResume
     *            是否续传
     * 
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, boolean autoResume, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, false, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param autoResume
     *            是否续传
     * @param autoRename
     *            是否自动重命名下载文件，根据HttpResponse中的head信息取得文件名
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, autoRename, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param params
     *            http请求参数
     * 
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, RequestParams params, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, params, false, false, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param params
     *            http请求参数
     * @param autoResume
     *            是否续传
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, false, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param params
     *            http请求参数
     * @param autoResume
     *            是否续传
     * @param autoRename
     *            是否自动重命名下载文件，根据HttpResponse中的head信息取得文件名
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(String url, String target, RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
        return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, autoRename, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * 
     * @param method
     *            http请求的方式
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param params
     *            http请求参数
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params, RequestCallBack<File> callback) {
        return download(method, url, target, params, false, false, callback);
    }
    
    /**
     * @Title: download
     * @Description:下载文件
     * 
     * @param method
     *            http请求的方式
     * @param url
     *            文件url地址
     * @param target
     *            文件存放路径
     * @param params
     *            http请求参数
     * @param autoResume
     *            是否续传
     * @param callback
     *            http响应的对调对象
     * @return
     */
    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
        return download(method, url, target, params, autoResume, false, callback);
    }
    
    // //////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @Description: TODO
     * @param HttpRequest
     *            .HttpMethod method 请求方法
     * @param String
     *            url 目标url地址
     * @param String
     *            target 存储路径
     * @param RequestParams
     *            params 请求参数
     * @param boolean autoResume 是否断点续传
     * @param boolean autoRename 是否自动重命名
     * @param RequestCallBack
     *            <File> callback 请求回调
     * @return HttpHandler<File>
     * @throws UnsupportedEncodingException
     * @note
     */
    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
        if(url == null)
            throw new IllegalArgumentException("url may not be null");
        if(target == null)
            throw new IllegalArgumentException("target may not be null");
        //
        HttpRequest request=new HttpRequest(method, url);
        HttpHandler<File> handler=new HttpHandler<File>(httpClient, httpContext, responseTextCharset, callback);
        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params, handler);
        if(params != null){
            handler.setPriority(params.getPriority());
        }
        handler.executeOnExecutor(EXECUTOR, request, target, autoResume, autoRename);
        return handler;
    }
    
    private <T> HttpHandler<T> sendRequest(HttpRequest request, RequestParams params, RequestCallBack<T> callBack) {
        HttpHandler<T> handler=new HttpHandler<T>(httpClient, httpContext, responseTextCharset, callBack);
        handler.setExpiry(currentRequestExpiry);// 下载后结果的缓存有效期
        handler.setHttpRedirectHandler(httpRedirectHandler);// 重定向处理
        //
        // 将requestParam对象包含信息塞进httprequest对象，并设置上传的回调RequestCallBackHandler（HttpHandler本身）。
        request.setRequestParams(params, handler);
        if(params != null){
            handler.setPriority(params.getPriority());// 任务优先级
        }
        handler.execute(request);
        return handler;
    }
    
    private ResponseStream sendSyncRequest(HttpRequest request, RequestParams params) throws HttpException {
        SyncHttpHandler handler=new SyncHttpHandler(httpClient, httpContext, responseTextCharset);
        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params);
        return handler.sendRequest(request);
    }
}
