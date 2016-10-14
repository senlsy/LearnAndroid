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
            //����LiveFolder�ṩ��ContentProvider
            intent.setData(MyContentProvider.LIVE_FOLDER_URI);
            // ����LiveFolders��ʾ��ģʽ
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE, LiveFolders.DISPLAY_MODE_LIST);
            // ����LiveFolders��ʾ��ͼƬ
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
            // ����LiveFolders��ʾ������
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, "LiveFolder");
            // -----------
            // ���LiveFolders��itemʱ�ᴥ��showActivity��Ϊ�����ݸ�Intent��
            // �������ΪContentProvider�Ļ�׼Uri���ڴ�����ʱ�򣬻��ѡ��item��_id��ӵ���intent��uri����
            intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT, new Intent(Intent.ACTION_VIEW, MyContentProvider.LIVE_FOLDER_URI));
            setResult(RESULT_OK, intent);
        }
        else
            setResult(RESULT_CANCELED);
        finish();
    }
}