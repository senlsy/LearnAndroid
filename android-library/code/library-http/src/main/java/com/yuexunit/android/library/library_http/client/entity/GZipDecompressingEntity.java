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
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;


/**
 * @ClassName: GZipDecompressingEntity
 * @Description:对HttpResponse实体HttpEntity.getContent得到的inputStream进行包装
 * @author Linsq
 * @date 2015年7月9日 下午2:51:52
 * 
 */
public class GZipDecompressingEntity extends DecompressingEntity
{
    
    public GZipDecompressingEntity(final HttpEntity entity){
        super(entity);
    }
    
    @Override
    InputStream decorate(final InputStream wrapped) throws IOException {
        //将InputStream流包装成GZIPInputStream流
        return new GZIPInputStream(wrapped);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Header getContentEncoding() {
        return null;
    }
}
