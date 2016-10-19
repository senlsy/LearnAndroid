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
package com.yuexunit.android.library.library_http.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.protocol.HTTP;

import com.yuexunit.android.library.library_http.base.RequestParams;
import com.yuexunit.android.library.library_http.callback.RequestCallBackHandler;
import com.yuexunit.android.library.library_http.client.entity.UploadEntity;
import com.yuexunit.android.library.library_http.util.OtherUtils;
import com.yuexunit.android.library.library_http.util.URIBuilder;
import com.yuexunit.android.library.library_utils.log.Logger;


/**
 * Author: wyouflf Date: 13-7-26 Time: 下午2:49
 */
public class HttpRequest extends HttpRequestBase implements HttpEntityEnclosingRequest
{
    
    private HttpEntity entity;// 请求实体
    private HttpMethod method;// 请求方法
    private URIBuilder uriBuilder;// url
    private Charset uriCharset;// url编码
    
    public HttpRequest(HttpMethod method){
        super();
        this.method=method;
    }
    
    public HttpRequest(HttpMethod method, String uri){
        super();
        this.method=method;
        setURI(uri);
    }
    
    public HttpRequest(HttpMethod method, URI uri){
        super();
        this.method=method;
        setURI(uri);
    }
    
    public HttpRequest addQueryStringParameter(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }
    
    public HttpRequest addQueryStringParameter(NameValuePair nameValuePair) {
        uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
        return this;
    }
    
    public HttpRequest addQueryStringParams(List<NameValuePair> nameValuePairs) {
        if(nameValuePairs != null){
            for(NameValuePair nameValuePair:nameValuePairs){
                uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
        return this;
    }
    
    // ------------------------------------
    /**
     * @Title: setRequestParams
     * @Description:将RequestParams对象包含的信息塞进HttpRequest请求。
     * @param param
     * @throws UnsupportedEncodingException
     */
    public void setRequestParams(RequestParams param) {
        if(param != null){
            if(uriCharset == null){
                uriCharset=Charset.forName(param.getCharset());
            }
            List<RequestParams.HeaderItem> headerItems=param.getHeaders();
            if(headerItems != null){
                for(RequestParams.HeaderItem headerItem:headerItems){
                    if(headerItem.overwrite){
                        this.setHeader(headerItem.header);
                    }
                    else{
                        this.addHeader(headerItem.header);
                    }
                }
            }
            this.addQueryStringParams(param.getQueryStringParams());
            this.setEntity(param.getEntity());
        }
    }
    
    /**
     * @Description: 将RequestParams对象包含的信息添加进HttpRequest请求。
     *               并设置上传进度的回调，RequestCallbackHandler。
     * @param
     * @return void
     * @throws UnsupportedEncodingException
     * @note
     */
    public void setRequestParams(RequestParams param, RequestCallBackHandler callBackHandler) {
        if(param != null){
            if(uriCharset == null){
                uriCharset=Charset.forName(param.getCharset());
            }
            // 添加报文头
            List<RequestParams.HeaderItem> headerItems=param.getHeaders();
            if(headerItems != null){
                for(RequestParams.HeaderItem headerItem:headerItems){
                    if(headerItem.overwrite){
                        this.setHeader(headerItem.header);
                    }
                    else{
                        this.addHeader(headerItem.header);
                    }
                }
            }
            // 添加查询串
            this.addQueryStringParams(param.getQueryStringParams());
            // 取出requestParams中的httpEntitiy，如果实现了UploadEntity接口，就为这个httpEntity实体设置上传回调对象。
            HttpEntity entity=param.getEntity();
            if(entity != null){
                if(entity instanceof UploadEntity){
                    ((UploadEntity)entity).setCallBackHandler(callBackHandler);
                }
                this.setEntity(entity);
            }
        }
    }
    
    @Override
    public URI getURI() {
        if(uriCharset == null){
            uriCharset=OtherUtils.getCharsetFromHttpRequest(this);// url地址编码，http
                                                                  // head设置编码优先
        }
        if(uriCharset == null){
            uriCharset=Charset.forName(HTTP.UTF_8);//
        }
        try{
            return uriBuilder.build(uriCharset);
        } catch(URISyntaxException e){
            Logger.w(e, false);
            return null;
        }
    }
    
    @Override
    public void setURI(URI uri) {
        this.uriBuilder=new URIBuilder(uri);
    }
    
    public void setURI(String uri) {
        this.uriBuilder=new URIBuilder(uri);
    }
    
    @Override
    public String getMethod() {
        return this.method.toString();
    }
    
    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    @Override
    public void setEntity(final HttpEntity entity) {
        this.entity=entity;
    }
    
    @Override
    public boolean expectContinue() {
        Header expect=getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        HttpRequest clone=(HttpRequest)super.clone();
        if(this.entity != null){
            clone.entity=(HttpEntity)CloneUtils.clone(this.entity);
        }
        return clone;
    }
    
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        MOVE("MOVE"),
        COPY("COPY"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE"),
        CONNECT("CONNECT");
        
        private final String value;
        
        HttpMethod(String value){
            this.value=value;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
}
