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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.yuexunit.android.library.library_http.client.multipart.content.ContentBody;
import com.yuexunit.android.library.library_utils.log.Logger;


/**
 * @ClassName: HttpMultipart
 * @Description: 复合数据类型中，每个子报文体（FromBody）的容器类。并在发送请求时，为每个子报文提添加分割线。
 * @author LinSQ
 * @date 2015-5-7 下午7:41:00
 * @version
 * @note
 */
class HttpMultipart
{
    
    /**
     * @Description: 用charset字符集对string进行编码，生成ByteArrayBuffer
     * @param
     * @return ByteArrayBuffer
     * @note
     */
    private static ByteArrayBuffer encode(final Charset charset, final String string) {
        ByteBuffer encoded=charset.encode(CharBuffer.wrap(string));
        ByteArrayBuffer bab=new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.position(), encoded.remaining());
        return bab;
    }
    
    private static void writeBytes(final ByteArrayBuffer b, final OutputStream out) throws IOException {
        out.write(b.buffer(), 0, b.length());
        out.flush();
    }
    
    private static void writeBytes(final String s, final Charset charset, final OutputStream out) throws IOException {
        ByteArrayBuffer b=encode(charset, s);
        writeBytes(b, out);
    }
    
    private static void writeBytes(final String s, final OutputStream out) throws IOException {
        ByteArrayBuffer b=encode(MIME.DEFAULT_CHARSET, s);
        writeBytes(b, out);
    }
    
    private static void writeField(final MinimalField field, final OutputStream out) throws IOException {
        writeBytes(field.getName(), out);
        writeBytes(FIELD_SEP, out);
        writeBytes(field.getBody(), out);
        writeBytes(CR_LF, out);
    }
    
    private static void writeField(final MinimalField field, final Charset charset, final OutputStream out) throws IOException {
        writeBytes(field.getName(), charset, out);
        writeBytes(FIELD_SEP, out);
        writeBytes(field.getBody(), charset, out);
        writeBytes(CR_LF, out);
    }
    
    private static final ByteArrayBuffer FIELD_SEP=encode(MIME.DEFAULT_CHARSET, ": ");
    private static final ByteArrayBuffer CR_LF=encode(MIME.DEFAULT_CHARSET, "\r\n");
    private static final ByteArrayBuffer TWO_DASHES=encode(MIME.DEFAULT_CHARSET, "--");
    private String subType;
    private final Charset charset;
    private final String boundary;
    //
    private final List<FormBodyPart> parts;
    private final HttpMultipartMode mode;
    
    public HttpMultipart(final String subType, final Charset charset, final String boundary, HttpMultipartMode mode){
        super();
        if(subType == null){
            throw new IllegalArgumentException("Multipart subtype may not be null");
        }
        if(boundary == null){
            throw new IllegalArgumentException("Multipart boundary may not be null");
        }
        this.subType=subType;
        this.charset=charset != null ? charset : MIME.DEFAULT_CHARSET;
        this.boundary=boundary;
        this.parts=new ArrayList<FormBodyPart>();
        this.mode=mode;
    }
    
    public HttpMultipart(final String subType, final Charset charset, final String boundary){
        this(subType, charset, boundary, HttpMultipartMode.STRICT);
    }
    
    public HttpMultipart(final String subType, final String boundary){
        this(subType, null, boundary);
    }
    
    public void setSubType(String subType) {
        this.subType=subType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public HttpMultipartMode getMode() {
        return this.mode;
    }
    
    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }
    
    public void addBodyPart(final FormBodyPart part) {
        if(part == null){
            return;
        }
        this.parts.add(part);
    }
    
    public String getBoundary() {
        return this.boundary;
    }
    
    private void doWriteTo(final HttpMultipartMode mode, final OutputStream out, boolean writeContent) throws IOException {
        doWriteTo(mode, out, MultipartEntity.CallBackInfo.DEFAULT, writeContent);
    }
    
    /**
     * Writes out the content in the multipart/form encoding. This method
     * produces slightly different formatting depending on its compatibility
     * mode.
     * 
     * @see #getMode()
     */
    public void writeTo(final OutputStream out, MultipartEntity.CallBackInfo callBackInfo) throws IOException {
        doWriteTo(this.mode, out, callBackInfo, true);
    }
    
    private void doWriteTo(final HttpMultipartMode mode, final OutputStream out, MultipartEntity.CallBackInfo callBackInfo, boolean writeContent) throws IOException {
        callBackInfo.pos=0;
        ByteArrayBuffer boundary=encode(this.charset, getBoundary());// 分割线
        //
        for(FormBodyPart part:this.parts){
            if( !callBackInfo.doCallBack(true)){
                throw new InterruptedIOException("cancel");
            }
            writeBytes(TWO_DASHES, out);// 写出“--”
            callBackInfo.pos+=TWO_DASHES.length();
            writeBytes(boundary, out);// 写出“分割线”
            callBackInfo.pos+=boundary.length();
            writeBytes(CR_LF, out);
            callBackInfo.pos+=CR_LF.length();// 换行
            //
            MinimalFieldHeader header=part.getHeader();// 子报文FromBoty包含的子报文头信息。
            
            
            switch(mode){
                case STRICT:
                    for(MinimalField field:header){
                        writeField(field, out);
                        callBackInfo.pos+=encode(MIME.DEFAULT_CHARSET, field.getName() + field.getBody()).length() + FIELD_SEP.length() + CR_LF.length();
                    }
                    break;
                case BROWSER_COMPATIBLE:
                    // Only write Content-Disposition
                    // Use content charset
                    MinimalField cd=header.getField(MIME.CONTENT_DISPOSITION);
                    writeField(cd, this.charset, out);
                    callBackInfo.pos+=encode(this.charset, cd.getName() + cd.getBody()).length() + FIELD_SEP.length() + CR_LF.length();
                    String filename=part.getBody().getFilename();
                    if(filename != null){
                        MinimalField ct=header.getField(MIME.CONTENT_TYPE);
                        writeField(ct, this.charset, out);
                        callBackInfo.pos+=encode(this.charset, ct.getName() + ct.getBody()).length() + FIELD_SEP.length() + CR_LF.length();
                    }
                    break;
                default:
                    break;
            }
            writeBytes(CR_LF, out);// 空白行
            callBackInfo.pos+=CR_LF.length();
            // 开始写报文主体
            if(writeContent){
                ContentBody body=part.getBody();
                body.setCallBackInfo(callBackInfo);// 为body设置写出回调
                body.writeTo(out);// 写出body
            }
            writeBytes(CR_LF, out);
            callBackInfo.pos+=CR_LF.length();
        }
        // 结尾
        writeBytes(TWO_DASHES, out);// 写出“--”
        callBackInfo.pos+=TWO_DASHES.length();
        writeBytes(boundary, out);// 写出“分割线”
        callBackInfo.pos+=boundary.length();
        writeBytes(TWO_DASHES, out);// 写出“--”
        callBackInfo.pos+=TWO_DASHES.length();
        writeBytes(CR_LF, out);// 换行
        callBackInfo.pos+=CR_LF.length();
        callBackInfo.doCallBack(true);// 回调
    }
    
    /**
     * Determines the total length of the multipart content (content length of
     * individual parts plus that of extra elements required to delimit the
     * parts from one another). If any of the @{link BodyPart}s contained in
     * this object is of a streaming entity of unknown length the total length
     * is also unknown.
     * <p/>
     * This method buffers only a small amount of data in order to determine the
     * total length of the entire entity. The content of individual parts is not
     * buffered.
     * 
     * @return total length of the multipart entity if known, <code>-1</code>
     *         otherwise.
     */
    public long getTotalLength() {
        long contentLen=0;
        for(FormBodyPart part:this.parts){
            ContentBody body=part.getBody();
            long len=body.getContentLength();
            if(len >= 0){
                contentLen+=len;
            }
            else{
                return -1;
            }
        }
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try{
            doWriteTo(this.mode, out, false);
            byte[] extra=out.toByteArray();
            return contentLen + extra.length;
        } catch(Throwable ignore){
            // Should never happen
            Logger.w(ignore, false);
            return -1;
        }
    }
}
