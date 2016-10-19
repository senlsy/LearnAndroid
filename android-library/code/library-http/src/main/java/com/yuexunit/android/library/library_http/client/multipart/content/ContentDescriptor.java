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

/**
 * @ClassName: ContentDescriptor
 * @Description: Content的描述信息。每个MIME类型由两部分组成，前面是数据的大类别，例如声音audio、图象image等，后面定义具体的种类。
 *               多用途互联网邮件扩展类型,例如 xml文档 .xml[text/xml],PNG图像.png[image/png]等
 * @author Linsq
 * @date 2015年7月10日 下午2:13:50
 * 
 */
public interface ContentDescriptor
{
    
    /**
     * Returns the body descriptors MIME type.
     *
     * @return The MIME type, which has been parsed from the content-type
     *         definition. Must not be null, but "text/plain", if no
     *         content-type was specified.
     * @see #getMediaType()
     * @see #getSubType()
     */
    String getMimeType();// 获取MIME类型
    
    /**
     * Gets the defaulted MIME media type for this content. For example
     * <code>TEXT</code>, <code>IMAGE</code>, <code>MULTIPART</code>
     *
     * @return the MIME media type when content-type specified, otherwise the
     *         correct default (<code>TEXT</code>)
     * @see #getMimeType()
     */
    String getMediaType();//获取大类别
    
    /**
     * Gets the defaulted MIME sub type for this content.
     *
     * @return the MIME media type when content-type is specified, otherwise the
     *         correct default (<code>PLAIN</code>)
     * @see #getMimeType()
     */
    String getSubType();//获取具体类别
    
    /**
     * <p>
     * The body descriptors character set, defaulted appropriately for the MIME
     * type.
     * </p>
     * <p>
     * For <code>TEXT</code> types, this will be defaulted to <code>UTF-8</code>
     * . For other types, when the charset parameter is missing this property
     * will be null.
     * </p>
     *
     * @return Character set, which has been parsed from the content-type
     *         definition. Not null for <code>TEXT</code> types, when unset will
     *         be set to default <code>UTF-8</code>. For other types, when
     *         unset, null will be returned.
     */
    String getCharset();//描述正文的编码
    
    /**
     * Returns the body descriptors transfer encoding.
     *
     * @return The transfer encoding. Must not be null, but "7bit", if no
     *         transfer-encoding was specified.
     */
    String getTransferEncoding();//传输编码
    
    /**
     * Returns the body descriptors content-length.
     *
     * @return Content length, if known, or -1, to indicate the absence of a
     *         content-length header.
     */
    long getContentLength();//正文的长度
}
