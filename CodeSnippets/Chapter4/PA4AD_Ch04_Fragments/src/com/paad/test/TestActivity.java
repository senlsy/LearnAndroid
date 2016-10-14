package com.paad.test;

import com.paad.fragments.R;
import com.paad.weatherstation.DetailsFragment;
import com.paad.weatherstation.MyListFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


public class TestActivity extends Activity implements OnClickListener
{
    
    public static int acount=0;
    FragmentManager fm;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Log.i("lintest", this.getClass().getSimpleName() + " onCreate");
        fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        MyListFragment listFragment=(MyListFragment)fm.findFragmentByTag("MyListFragment");
        if(listFragment == null)
        {
            Log.e("lintest", "nullnullnullnullnullnullnullnull");
            ft.add(R.id.testContainer, new MyListFragment(), "MyListFragment");
        }
        else
        {
            if(listFragment.isDetached()){
                Log.e("lintest", "--------------------------");
                ft.attach(listFragment);
            }
            else
            {
                Log.e("lintest", "**************************");
            }
        }
        ft.commit();
        // listFragment��ʾ ���btn2��
        // listFragment detach����������detach״̬�����ɼ�״̬��
        // detailFragment add����������attach״̬���ɼ�״̬����
        /* �����detach����������ָfragment�������ڵ�detach�����ֻ����������ʾ������ѣ�
         * ��������fragmentManager�Ĺ���Χ�� */
        // ���ã��ߵ���Ļ��------------
        /* ���ٹ��̣�listFragment���������룬detailFragment���������� */
        // detach��FragmentManager��framgent�����٣��ȣ�����detach״̬��fragment�����٣�
        // ִ�к�������������onSaveInstanceState(),onDestroy(),onDetacch()]
        /* [����Ļ�ϵ�fragment������(��:����attach״̬�ĺ�����)
         * ִ�е�������������onSaveInstanceState->onPause
         * ->onStop->onDetoryView->onDestroy->onDetacch�� */
        // �ָ����̣�
        // �������ٵı��Ȼָ�����һ�ָ�������detach״̬����attach״̬��Fragment�������³�ʼ�����ˣ���ִ����onCreate��
        // listFragment���³�ʼ��onAttach��onCreate��detailFragment���³�ʼ��onAttach��onCreate��
        // onAttachʱ��������ӽ�fragmentManager
        /* ��ʼִ��Activity��onCreate��
         * listFragment��Ȼ����ӽ�FragmentManager���������ʾ����Ļ����ΪlistFragemtn����detach״̬
         * detailFragment�ᱻ�ָ���ʾ����ΪdetailFragment����attach״̬�� [
         * listFragment����Activity��onCreate�Ĳ���������attach�������ӽ�������
         * listFragemnt����attach״̬ ] */
        // �ٴ����ã��ߵ���Ļ��------------
        // ��ͬ����Ļ�ϣ�������ӵ��������٣�listFragment�����١�detailFragment�����١�
        // �������ٵģ����Ȼָ���listFragment�Ȼָ���detailFragment��ָ���
        // �ٴ�����------------
        // ��ͬ����Ļ�ϣ�������ӵ��������٣�listFragment�����١�detailFragment�����١�
        // �������ٵģ����Ȼָ���listFragment�Ȼָ���detailFragment��ָ���
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn)
        {
            FragmentTransaction ft=fm.beginTransaction();
            DetailsFragment detailFragment=(DetailsFragment)fm.findFragmentByTag("DetailsFragment");
            if(detailFragment != null && !detailFragment.isDetached())
            {
                ft.detach(detailFragment);
            }
            MyListFragment listFragment=(MyListFragment)fm.findFragmentByTag("MyListFragment");
            if(listFragment == null)
            {
                ft.add(R.id.testContainer, new MyListFragment(), "MyListFragment");
            }
            else
            {
                // ����detach�����fragment�����ǹ���fragmentManager����
                // add(listFragment);����Ч�ġ�
                ft.attach(listFragment);
            }
            ft.commit();
        }
        else if(v.getId() == R.id.btn2)
        {
            // -----------
            FragmentTransaction ft=fm.beginTransaction();
            MyListFragment listFragment=(MyListFragment)fm.findFragmentByTag("MyListFragment");
            if(listFragment != null && !listFragment.isDetached())
            {
                ft.detach(listFragment);
            }
            DetailsFragment detailFragment=(DetailsFragment)fm.findFragmentByTag("DetailsFragment");
            if(detailFragment == null)
            {
                ft.add(R.id.testContainer, new DetailsFragment(), "DetailsFragment");
            }
            else
            {
                ft.attach(detailFragment);
            }
            ft.commit();
        }
        else if(v.getId() == R.id.btn3)
        {
            FragmentTransaction ft=fm.beginTransaction();
            MyListFragment listFragment=(MyListFragment)fm.findFragmentByTag("MyListFragment");
            if( !listFragment.isHidden())
            {
                ft.hide(listFragment);// ��������fragment���κ��������ں����ص�
            }
            else
            {
                ft.show(listFragment);
            }
            ft.commit();
        }
    }
    
    @Override
    protected void onStart() {
        Log.i("lintest", this.getClass().getSimpleName() + " onStart");
        super.onStart();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("lintest", this.getClass().getSimpleName() + " onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
        Log.i("lintest", this.getClass().getSimpleName() + " onResume");
        super.onResume();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("lintest", this.getClass().getSimpleName() + " onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
        Log.i("lintest", this.getClass().getSimpleName() + " onPause");
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        Log.i("lintest", this.getClass().getSimpleName() + " onStop");
        super.onStop();
    }
    
    @Override
    protected void onRestart() {
        Log.i("lintest", this.getClass().getSimpleName() + " onRestart");
        super.onRestart();
    }
    
    @Override
    protected void onDestroy() {
        Log.i("lintest", this.getClass().getSimpleName() + " onDestroy");
        super.onDestroy();
    }
}