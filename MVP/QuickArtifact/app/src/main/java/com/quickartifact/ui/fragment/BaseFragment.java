package com.quickartifact.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickartifact.R;
import com.quickartifact.constant.BundleKey;
import com.quickartifact.manager.session.SessionManager;
import com.quickartifact.utils.device.PermissionsEnum;
import com.quickartifact.utils.device.PermissionsUtils;

/**
 * Description: Fragment基类
 *
 * @author mark.lin
 * @date 2016/9/18 15:10
 */
public class BaseFragment extends Fragment {

    protected String mTag = getClass().getSimpleName();
    /**
     * 存放临时数据的bundle
     */
    private Bundle mSaveBundle;

    /**
     * fragment管理类实例
     */
    private FragmentManager mFragmentManager;

    /**
     * 请求权限的requestCode
     */
    private int mPermissionReqCode = hashCode();

    /**
     * 1、在实例化BaseFragment时，初始化一个bundle进Arguments
     */
    public BaseFragment() {
        initSaveBundle();
    }

    @CallSuper
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 1、初始化了mFragmentManager实例<br/>
     * 2、调用了initData(getActivity.getIntent());Intent属于会被消耗的
     */
    @CallSuper
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        initData(getActivity().getIntent());
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    /**
     * 1、检查是否含有临时保存的数据<br/>
     * 2、运行时权限申请<br/>
     */
    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkHasSaveBundle();
        PermissionsUtils.checkPermission(mPermissionReqCode, getActivity(), requestPermissions());
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 1、设置SessionManager当前页面为当前页面。<br/>
     */
    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        SessionManager.getInstance().setCurrentPageName(mTag);
    }

    /**
     * 1、保存需要临时存储的数据到Arguments
     */
    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    /**
     * 1、设置SessionManager上一个页面为当前页面。<br/>
     */
    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        SessionManager.getInstance().setPrePageName(mTag);
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 1、保存需要临时存储的数据
     */
    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
    }


    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onDetach() {
        super.onDetach();
    }

    //========================
    //回调子类的方法
    //========================

    /**
     * 传递进activity的Intnet，在Fragment.onCreate之前被调用。<br/>
     * 1、不可在该方法类操作view，view还未被绘制。
     */
    public void initData(Intent intent) {
    }

    /**
     * 运行时权限申请
     *
     * @return 返回需要申请的权限，权限定义在PermissionsEnum枚举类中
     */
    public PermissionsEnum[] requestPermissions() {
        return null;
    }

    /**
     * @param bundle 可以将需要保存的临时数据放入参数bundle里。
     */
    @CallSuper
    protected void onSaveState(Bundle bundle) {
        bundle.putInt(BundleKey.INTENT_EXTRA_FRAGMENT_PERMISSIONS_REQUEST_CODE, mPermissionReqCode);
    }

    /**
     * fragemnt含有临时保存的数据，恢复之后的回调。<br/>
     * 1、在Fragemnt.onActivityCreated()被调用，可以直接操作view。<br/>
     *
     * @param saveBundle 存放之前保存的临时数据
     */
    @CallSuper
    protected void onRestoreState(Bundle saveBundle) {
        mPermissionReqCode = saveBundle.getInt(BundleKey.INTENT_EXTRA_FRAGMENT_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * fragemnt不含有临时保存数据的回调，在Fragemnt.onActivityCreated()里被调用。<br/>
     * 1、第一次加载是不含有恢复数据的。<br/>
     * 2、由于接下来默认保存了mPermissionReqCode这个临时数据，fragment在恢复或重新add时都不会调用该方法。<br/>
     */
    protected void onFirstTimeLaunch() {

    }

    //========================
    //子类可调用的方法
    //========================

    /**
     * fragment 切换，采用hide|show方式
     *
     * @param from   当前是哪个fragment
     * @param to     要跳转到哪个fragment
     * @param bundle 携带的数据
     */
    protected final void switchContent(Fragment from, Fragment to, Bundle bundle) {

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
     * 显示fragment，并添加进事务，采用replace方式
     */
    protected final void showFragmentAddStack(Fragment fragment) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, fragment);
        if (!mFragmentManager.isDestroyed() && !fragment.isAdded()) {
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * popBack出一个事务
     */
    protected final void popStack() {
        mFragmentManager.popBackStack();
    }

    /**
     * 关闭当前activity
     */
    protected final void finishActivity() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * 调用activity.setResult()
     */
    protected final void setResultOK() {
        setResultOK(null);
    }

    /**
     * *
     * 调用activity.setResult()
     *
     * @param intent 回传数据
     */
    protected final void setResultOK(Intent intent) {
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
        }
    }


    /**
     * 获取当前的mFragemntManager实例
     */
    protected FragmentManager getCurrentFragmentManager() {
        return mFragmentManager;
    }

    //========================
    //自身业务方法
    //========================

    /**
     * 申请权限的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.mPermissionReqCode == requestCode) {
            PermissionsUtils.onRequestPermissionsResult(getActivity(), permissions, grantResults);
        }
    }

    ///////////////////////////////
    //将临时数据保存进Arguments,主要是为了能够恢复被repalce的fragment实例。 不然Fragemnt.onSaveInstanceState()就可以完成恢复
    //////////////////////////////

    /**
     * 在实例化BaseFragment时，初始化一个bundle进Arguments
     */
    private void initSaveBundle() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    /**
     * 判断fragment是否含有临时保存的数据，有就回调fragment.onRestoreState(mSaveBundle)
     */
    private void checkHasSaveBundle() {
        Bundle b = getArguments();
        if (b != null) {
            mSaveBundle = b.getBundle(BundleKey.INTENT_EXTRA_FRAGMENT_SAVE_BUNDLE_KEY);
            if (mSaveBundle != null) {
                onRestoreState(mSaveBundle);
                return;
            }
        }
        onFirstTimeLaunch();
    }

    /**
     * 将需要保存的临时数据，保存进setArguments()。
     */
    private void saveStateToArguments() {

        if (getView() != null) {
            mSaveBundle = new Bundle();
            onSaveState(mSaveBundle);
        }

        if (mSaveBundle != null) {
            Bundle b = getArguments();
            if (b != null) {
                b.putBundle(BundleKey.INTENT_EXTRA_FRAGMENT_SAVE_BUNDLE_KEY, mSaveBundle);
            }
        }
    }

}
