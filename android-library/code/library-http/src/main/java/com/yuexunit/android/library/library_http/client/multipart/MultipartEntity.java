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
package com.yuexunit.android.library.library_http.client.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.yuexunit.android.library.library_http.callback.RequestCallBackHandler;
import com.yuexunit.android.library.library_http.client.entity.UploadEntity;
import com.yuexunit.android.library.library_http.client.multipart.content.ContentBody;


/**
 * Multipart/form coded HTTP entity consisting of multiple body parts.
 * 
 * @since 4.0
 */
public class MultipartEntity implements HttpEntity, UploadEntity
{
    
    // 上传回调信息对象。主要是用来记录上传的信息，和将回调转发给其RequestCallBackHandler成员
    private CallBackInfo callBackInfo=new CallBackInfo();
    
    @Override
    public void setCallBackHandler(RequestCallBackHandler callBackHandler) {
        // 实现了UploadEentity接口，表明该MultipartEntity会有上传回调处理。
        callBackInfo.callBackHandler=callBackHandler;
    }
    
    /**
     * @ClassName: CallBackInfo
     * @Description: 用来记录上传的进度信息，以及包含上传回调的对象RequestCallBackHandler
     * @author LinSQ
     * @date 2015-5-9 下午4:24:40
     * @version
     * @note
     */
    public static class CallBackInfo
    {
        
        public final static CallBackInfo DEFAULT=new CallBackInfo();
        public RequestCallBackHandler callBackHandler=null;
        public long totalLength=0;// 记录上传报文中长度
        public long pos=0;// 记录以上传的长度
        
        /**
         * @Description: 
         *               转发回到，调用了RequestCallBackHandler.updateProgress(totalLength
         *               , pos, forceUpdateUI)
         * @param forceUpdateUI是否立即更细UI
         * @return boolean
         * @note
         */
        public boolean doCallBack(boolean forceUpdateUI) {
            if(callBackHandler != null){
                return callBackHandler.updateProgress(totalLength, pos, forceUpdateUI);
            }
            return true;
        }
    }
    
    //
    private final static char[] MULTIPART_CHARS="-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    // requestParams中的如果常出现不同的实体类容，则实体内容会被取出，并封装成FromBody被添加进multipart对象中。
    private final HttpMultipart multipart;
    private long length;// multipart的长度。
    private volatile boolean dirty;// 标志位，表示是否时候需要重新计算multipart的长度。
    //
    // 复合数据类型的报文头信息[Content-Type：multipart/子类型；boundary=（分割线。。。）]
    private Header contentType;
    private String multipartSubtype="form-data";// 子类型
    //
    private final String boundary;
    private final Charset charset;// MultipartEntity的字符编码。
    
    public MultipartEntity(final HttpMultipartMode mode){
        this(mode, null, null);
    }
    
    public MultipartEntity(){
        this(HttpMultipartMode.STRICT, null, null);
    }
    
    public MultipartEntity(HttpMultipartMode mode, String boundary, Charset charset){
        super();
        if(boundary == null){
            boundary=generateBoundary();
        }
        this.boundary=boundary;
        if(mode == null){
            mode=HttpMultipartMode.STRICT;
        }
        this.charset=charset != null ? charset : MIME.DEFAULT_CHARSET;
        this.multipart=new HttpMultipart(multipartSubtype, this.charset, this.boundary, mode);
        this.contentType=new BasicHeader(HTTP.CONTENT_TYPE, generateContentType(this.boundary, this.charset));
        this.dirty=true;
    }
    
    public void setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype=multipartSubtype;
        this.multipart.setSubType(multipartSubtype);
        this.contentType=new BasicHeader(HTTP.CONTENT_TYPE, generateContentType(this.boundary, this.charset));
    }
    
    protected String generateContentType(final String boundary, final Charset charset) {
        StringBuilder buffer=new StringBuilder();
        buffer.append("multipart/" + multipartSubtype + "; boundary=");
        buffer.append(boundary);
        /* if (charset != null) { buffer.append("; charset=");
         * buffer.append(charset.name()); } */
        return buffer.toString();
    }
    
    protected String generateBoundary() {
        StringBuilder buffer=new StringBuilder();
        Random rand=new Random();
        int count=rand.nextInt(11) + 30; // a random size from 30 to 40
        for(int i=0;i < count;i++){
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
    
    // ----------
    public void addPart(final String name, final ContentBody contentBody) {
        addPart(new FormBodyPart(name, contentBody));
    }
    
    public void addPart(final String name, final ContentBody contentBody, final String contentDisposition) {
        addPart(new FormBodyPart(name, contentBody, contentDisposition));
    }
    
    public void addPart(final FormBodyPart bodyPart) {
        this.multipart.addBodyPart(bodyPart);
        this.dirty=true;
    }
    
    // HttpEntity的实现----------
    @Override
    public boolean isRepeatable() {
        for(FormBodyPart part:this.multipart.getBodyParts()){
            ContentBody body=part.getBody();
            if(body.getContentLength() < 0){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isChunked() {
        return !isRepeatable();
    }
    
    @Override
    public boolean isStreaming() {
        return !isRepeatable();
    }
    
    @Override
    public long getContentLength() {
        if(this.dirty){
            this.length=this.multipart.getTotalLength();
            this.dirty=false;
        }
        return this.length;
    }
    
    @Override
    public Header getContentType() {
        return this.contentType;
    }
    
    @Override
    public Header getContentEncoding() {
        return null;
    }
    
    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if(isStreaming()){
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }
    
    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        callBackInfo.totalLength=getContentLength();
        this.multipart.writeTo(outStream, callBackInfo);
    }
}
