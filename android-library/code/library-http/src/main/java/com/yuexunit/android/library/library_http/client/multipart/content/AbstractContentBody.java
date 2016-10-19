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
package com.yuexunit.android.library.library_http.client.multipart.content;

import com.yuexunit.android.library.library_http.client.multipart.MultipartEntity;


/**
 * @since 4.0
 */
public abstract class AbstractContentBody implements ContentBody
{
    
    // 每个MIME类型由两部分组成，前面是数据的大类别，例如声音audio、图象image等，后面定义具体的种类。
    // 多用途互联网邮件扩展类型,例如 xml文档 .xml[text/xml],PNG图像.png[image/png]等
    private final String mimeType;
    private final String mediaType;
    private final String subType;
    // 上传回调的处理，该对象包含了RequestCallbackHandler成员对象
    protected MultipartEntity.CallBackInfo callBackInfo=MultipartEntity.CallBackInfo.DEFAULT;
    
    public AbstractContentBody(final String mimeType){
        super();
        if(mimeType == null){
            throw new IllegalArgumentException("MIME type may not be null");
        }
        this.mimeType=mimeType;
        int i=mimeType.indexOf('/');
        if(i != -1){
            this.mediaType=mimeType.substring(0, i);
            this.subType=mimeType.substring(i + 1);
        }
        else{
            this.mediaType=mimeType;
            this.subType=null;
        }
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    @Override
    public void setCallBackInfo(MultipartEntity.CallBackInfo callBackInfo) {
        this.callBackInfo=callBackInfo;
    }
}
