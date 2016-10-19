package com.quickartifact.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.quickartifact.R;
import com.quickartifact.manager.session.SessionManager;
import com.quickartifact.utils.UmengUtils;

import java.util.List;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Description: Activity基类，集成一些常用方法
 *
 * @author mark.lin
 * @date 2016/9/18 15:08
 */
public abstract class BaseActivity extends SwipeBackActivity {

    FragmentManager mFragmentManager;

    /**
     * 1、调用了initData(getIntent())<br/>
     * 2、初始化了mFragmentManager实例<br/>
     * 3、先回调getLayoutView(),为null是再回调getLayoutId()，设置ContentView。<br/>
     * 4、绑定View --> ButterKnife.bind(this);<br/>
     * 5、调用initView()<br/>
     */
    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent传递的数据
        initData(getIntent());
        mFragmentManager = getSupportFragmentManager();

        //初始化界面
        View contentView = getLayoutView();
        if (contentView != null) {
            setContentView(contentView);
        } else {
            setContentView(getLayoutId());
        }
        ButterKnife.bind(this);
        initView(savedInstanceState);

    }

    @CallSuper
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 1、调用了友盟统计UmengUtils.onResume(this)
     */
    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        UmengUtils.onResume(this);
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * 1、调用了友盟统计UmengUtils.onPause(this);
     */
    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        UmengUtils.onPause(this);
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
    }

    @CallSuper
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //============================
    // 回调子类的方法
    //============================

    /**
     * 配置ContentView
     */
    public View getLayoutView() {
        return null;
    }

    /**
     * 配置ContentView
     */
    public abstract int getLayoutId();

    /**
     * 传进的Intnet，在Activity.setContentView()之前被调用。<br/>
     * 1、不可在该方法类操作view，view未被绘制。
     */
    public void initData(Intent intent) {
    }

    /**
     * 初始化view，在activity.setContentView()之后调用
     *
     * @param savedInstanceState
     */
    public void initView(Bundle savedInstanceState) {

    }


    //============================
    //子类调用的方法
    //============================


    /**
     * 显示fragment,采用replace方式
     */
    protected final void showFragment(Fragment fragment) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (!mFragmentManager.isDestroyed() && !fragment.isAdded()) {
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * fragment 切换，采用hiden|show方式
     *
     * @param from 当前是哪个fragment
     * @param to   要跳转到哪个fragment
     */
    protected void switchContent(Fragment from, Fragment to) {
        switchContent(from, to, null);
    }

    /**
     * fragment 切换，切换，采用hiden|show方式
     *
     * @param from   当前是哪个fragment
     * @param to     要跳转到哪个fragment
     * @param bundle 携带的数据
     */
    protected void switchContent(Fragment from, Fragment to, Bundle bundle) {

        if (to != null) {
            SessionManager.getInstance().setCurrentPageName(to.getClass().getSimpleName());
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 如果当前的fragment为null,则直接显示to fragment
        if (from == null) {
            showFragment(to);
            return;
        }

        // 如果from已经添加 则隐藏
        if (from.isAdded()) {
            transaction.hide(from);
        }

        // 如果to 已经添加 则显示
        if (to.isAdded()) {
            transaction.show(to);
        } else {
            // 如果TO未添加则添加
            to.setArguments(bundle);
            transaction.add(R.id.fragment_container, to);
        }
        if (!mFragmentManager.isDestroyed()) {
            transaction.commitAllowingStateLoss();
        }
    }

    //============================
    // 自身业务逻辑方法
    //============================

    /**
     * 调用每个该页面含有的fragment的onActivityResult()方法
     */
    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = mFragmentManager.getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    /**
     * 调用每个该页面含有的fragment的onRequestPermissionsResult()方法
     */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = mFragmentManager.getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    /**
     * 点击在页面空白处，让键盘隐藏起来
     */
    @CallSuper
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {

            int[] position = {0, 0};
            v.getLocationInWindow(position);
            int left = position[0];
            int top = position[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            if (event.getX() > left &&
                    event.getX() < right &&
                    event.getY() > top &&
                    event.getY() < bottom) {
                //发生在EditText上不用理会
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 点击空白位置，自动隐藏软键盘
     */
    /*public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }*/


}
