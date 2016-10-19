package com.yuexunit.android.library.library_http;

import java.io.File;

import android.R;

import com.yuexunit.android.library.library_http.base.RequestParams;
import com.yuexunit.android.library.library_http.base.ResponseInfo;
import com.yuexunit.android.library.library_http.callback.RequestCallBack;
import com.yuexunit.android.library.library_http.client.HttpRequest;
import com.yuexunit.android.library.library_http.exception.HttpException;
import com.yuexunit.android.library.library_utils.log.UploadE_api;


public class UpLoadE_impl implements UploadE_api
{
    
    @Override
    public void uploadErrorFile(String url, File uploadFile) {
        HttpUtils http=new HttpUtils();
        RequestParams params=new RequestParams();
        params.setReportServer(false);
        params.addBodyParameter("file", uploadFile);
        http.send(HttpRequest.HttpMethod.POST, url, params, new
                  RequestCallBack<String>(){
                      
                      @Override
                      public void onStart() {
                      }
                      
                      @Override
                      public void onFailure(HttpException arg0, String arg1) {
                      }
                      
                      @Override
                      public void onSuccess(ResponseInfo<String> responseInfo) {
                      }
                  });
    }
}
