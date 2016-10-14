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
        // listFragment显示 点击btn2，
        // listFragment detach出容器属于detach状态（不可见状态）
        // detailFragment add进容器属于attach状态（可见状态）。
        /* 这里的detach动作并不是指fragment生命周期的detach概念，而只是脱离了显示界面而已，
         * 它还是在fragmentManager的管理范围里 */
        // 重置（颠倒屏幕）------------
        /* 销毁过程：listFragment先销毁脱离，detailFragment后销毁脱离 */
        // detach在FragmentManager的framgent先销毁（既：属于detach状态的fragment先销毁）
        // 执行后续的生命周期onSaveInstanceState(),onDestroy(),onDetacch()]
        /* [在屏幕上的fragment后销毁(既:属于attach状态的后销毁)
         * 执行的销毁生命周期onSaveInstanceState->onPause
         * ->onStop->onDetoryView->onDestroy->onDetacch。 */
        // 恢复过程：
        // （先销毁的被先恢复。这一恢复不管是detach状态还是attach状态的Fragment都被重新初始化过了，都执行了onCreate）
        // listFragment重新初始化onAttach，onCreate。detailFragment重新初始化onAttach，onCreate。
        // onAttach时被重新添加进fragmentManager
        /* 开始执行Activity的onCreate：
         * listFragment虽然被添加进FragmentManager里，但并不显示在屏幕上因为listFragemtn处于detach状态
         * detailFragment会被恢复显示，因为detailFragment属于attach状态。 [
         * listFragment根据Activity的onCreate的操作又重新attach至到可视界面上了
         * listFragemnt属于attach状态 ] */
        // 再次重置（颠倒屏幕）------------
        // （同在屏幕上，最新添加的最先销毁）listFragment先销毁。detailFragment后销毁。
        // （先销毁的，被先恢复）listFragment先恢复，detailFragment后恢复。
        // 再次重置------------
        // （同在屏幕上，最新添加的最先销毁）listFragment先销毁。detailFragment后销毁。
        // （先销毁的，被先恢复）listFragment先恢复，detailFragment后恢复。
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
                // 调用detach分离的fragment，还是归于fragmentManager管理。
                // add(listFragment);是无效的。
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
                ft.hide(listFragment);// 不会引起fragment的任何生命周期函数回调
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