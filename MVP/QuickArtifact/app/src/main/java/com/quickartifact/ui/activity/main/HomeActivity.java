package com.quickartifact.ui.activity.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;

import com.quickartifact.R;
import com.quickartifact.exception.check.BreakActionException;
import com.quickartifact.manager.cache.trans_cache.BitmapCompressCache;
import com.quickartifact.manager.cache.trans_cache.BitmapDownloadCache;
import com.quickartifact.ui.activity.BaseActivity;
import com.quickartifact.utils.device.PermissionsEnum;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.image.ImageUtils;
import com.quickartifact.utils.log.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.bt1)
    Button mButton1;

    @BindView(R.id.bt2)
    Button mButton2;

    @BindView(R.id.bt3)
    Button mButton3;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        LogUtils.checkParameterNull(mButton1, mButton2, mButton3);
    }

    @Override
    public PermissionsEnum[] requestPermissions() {
        return new PermissionsEnum[]{PermissionsEnum.READ_PHONE_STATE, PermissionsEnum.WRITE_EXTERNAL_STORAGE};
    }

    @OnClick(R.id.bt1)
    public void btn1OnClick() {
        FileUtils.writeLogToFile("hello word!");
    }

    @OnClick(R.id.bt2)
    public void btn2OnClick() {
        FileUtils.wirteExceptionToFile(new BreakActionException("this a test"));
    }

    @OnClick(R.id.bt3)
    public void btn3OnClick() {
        final String url = "http://attach.bbs.miui.com/forum/201401/11/145731k33nz338zind43pp.jpg";
        LogUtils.i("start download");
        ImageUtils.downloadImage(url, BitmapDownloadCache.getInstance(), new ImageUtils.ImageDownloadCallback() {
            @Override
            public void finish(Bitmap bitmap) {
                LogUtils.i("finish");
                BitmapCompressCache.getInstance().put(url, BitmapDownloadCache.getInstance().get(url));
            }

            @Override
            public void error(Exception exception) {
                LogUtils.i("error");
            }
        });
    }


}
