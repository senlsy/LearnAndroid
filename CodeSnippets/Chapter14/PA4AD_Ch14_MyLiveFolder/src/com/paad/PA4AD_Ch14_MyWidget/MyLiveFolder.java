package com.paad.PA4AD_Ch14_MyWidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.LiveFolders;


public class MyLiveFolder extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action=getIntent().getAction();
        if(LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action)){
            Intent intent=new Intent();
            //设置LiveFolder提供的ContentProvider
            intent.setData(MyContentProvider.LIVE_FOLDER_URI);
            // 设置LiveFolders显示的模式
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE, LiveFolders.DISPLAY_MODE_LIST);
            // 设置LiveFolders显示的图片
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
            // 设置LiveFolders显示的名称
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, "LiveFolder");
            // -----------
            // 点击LiveFolders的item时会触发showActivity行为并传递该Intent。
            // 如果设置为ContentProvider的基准Uri，在触发的时候，会把选中item的_id添加到该intent的uri后面
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT, new Intent(Intent.ACTION_VIEW, MyContentProvider.LIVE_FOLDER_URI));
            setResult(RESULT_OK, intent);
        }
        else
            setResult(RESULT_CANCELED);
        finish();
    }
}