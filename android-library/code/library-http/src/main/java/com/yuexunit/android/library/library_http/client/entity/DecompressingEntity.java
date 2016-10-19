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
package com.yuexunit.android.library.library_http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import com.yuexunit.android.library.library_http.callback.RequestCallBackHandler;
import com.yuexunit.android.library.library_http.util.IOUtils;


/**
 * Common base class for decompressing {@link org.apache.http.HttpEntity}
 * implementations.
 * 
 * @since 4.1
 */
abstract class DecompressingEntity extends HttpEntityWrapper implements UploadEntity
{
    
    public DecompressingEntity(final HttpEntity wrapped){
        super(wrapped);// 被包装的实体
        this.uncompressedLength=wrapped.getContentLength();// 未压缩长度
    }
    
    /**
     * @Description: 对实体流进行一次包装，变成包装流
     * @param
     * @return InputStream
     * @note
     */
    abstract InputStream decorate(final InputStream wrapped) throws IOException;
    
    private InputStream getDecompressingStream() throws IllegalStateException, IOException {
        InputStream in=null;
        try{
            in=wrappedEntity.getContent();// 得到实体流
            return decorate(in);// 对实体流进行包装
        } catch(IOException ex)
        {
            IOUtils.closeQuietly(in);
            throw ex;
        }
    }
    
    private InputStream content;
    
    @Override
    public InputStream getContent() throws IOException {
        if(wrappedEntity.isStreaming()){
            if(content == null){
                content=getDecompressingStream();// 包装后的实体流
            }
            return content;
        }
        else{
            return getDecompressingStream();// 包装后的实体流
        }
    }
    
    @Override
    public long getContentLength() {
        /* length of compressed content is not known */
        return -1;
    }
    
    private long uncompressedLength;
    private long uploadedSize=0;
    
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        // 在httpClient发送报文的时候才被调用。读取实体内容，写出到outputStream
        if(outStream == null){
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream inStream=null;
        try{
            inStream=getContent();
            byte[] tmp=new byte[1024 * 4];
            int len;
            while((len=inStream.read(tmp)) != -1){
                outStream.write(tmp, 0, len);
                uploadedSize+=len;
                if(callBackHandler != null){
                    if( !callBackHandler.updateProgress(uncompressedLength, uploadedSize, false)){
                        throw new InterruptedIOException("cancel");
                    }
                }
            }
            outStream.flush();
            if(callBackHandler != null){
                callBackHandler.updateProgress(uncompressedLength, uploadedSize, true);
            }
        } finally{
            IOUtils.closeQuietly(inStream);
        }
    }
    
    private RequestCallBackHandler callBackHandler=null;
    
    @Override
    public void setCallBackHandler(RequestCallBackHandler callBackHandler) {
        this.callBackHandler=callBackHandler;
    }
}
