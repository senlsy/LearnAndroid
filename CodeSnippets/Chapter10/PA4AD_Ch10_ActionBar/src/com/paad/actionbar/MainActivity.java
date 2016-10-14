package com.paad.actionbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnClickListener
{
    
    private RelativeLayout mRLayout;
    
    private Button mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRLayout=(RelativeLayout)findViewById(R.id.content);
        mBtn1=(Button)findViewById(R.id.btn1);
        mBtn2=(Button)findViewById(R.id.btn2);
        mBtn3=(Button)findViewById(R.id.btn3);
        mBtn4=(Button)findViewById(R.id.btn4);
        mBtn5=(Button)findViewById(R.id.btn5);
        mBtn6=(Button)findViewById(R.id.btn6);
        mBtn7=(Button)findViewById(R.id.btn7);
        mBtn8=(Button)findViewById(R.id.btn8);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn7.setOnClickListener(this);
        mBtn8.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.btn1:
                // ��ʾ״̬����Activity���ֲ�ȫ����ʾ
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                break;
            case R.id.btn2:
                // �Ƴ�״̬����ͬʱActivity���ֻ���չȫ����ʾ
                mRLayout.setSystemUiVisibility(View.INVISIBLE);
                break;
            case R.id.btn3:
                // Activity���ֻ���չȫ����ʾ����״̬�����Ƴ�
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                break;
            case R.id.btn4:
                // Activity���ֻ���չȫ����ʾ����״̬�����ᱻ�Ƴ���
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                break;
            case R.id.btn5:
                // Activity���ֻ���չȫ����ʾ����״̬�����ᱻ�Ƴ���
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                break;
            case R.id.btn6:
                // Activity���ֻ���չȫ����ʾ����״̬�����ᱻ�Ƴ���
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
                break;
            case R.id.btn7:
                // �Ƴ����������ֻ���
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                break;
            case R.id.btn8:
                // ������ʾ��������������״̬��һЩ��Ϣ���ֻ�����
                // ������ʾ������ť��������ϵͳ����һЩ ״̬��Ϣ��
                mRLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                break;
        }
    }
}