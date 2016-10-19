package frogermcs.io.githubclient.ui.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import frogermcs.io.githubclient.exception.InitializationException;
import frogermcs.io.githubclient.persenter.BasePersenter;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
public abstract class BaseActivity extends AppCompatActivity implements MVP_View {

    private BasePersenter mPersenter;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent();
        mPersenter = getPersenter();
        if (mPersenter == null) {
            throw new InitializationException("Persenter variable must be Initialization");
        }
        mPersenter.attachView(this);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPersenter != null) {
            mPersenter.detachView(this);
        }
    }

    //=======================
    //回调子类的方法
    //=======================

    /**
     * 完成component实例的创建及必需完成persenter对象的注入<br/>
     * 1、该方法中使用persenter方法请注意，view还没有传递到persenter实例中
     * 2、该方法执行后会检查persenter实例是有有生成。请在getPersenter()返回该页面对应的persenter实例
     * 3、getPersenter()执行完毕之后才会将view传递进persenter实例中
     */
    protected abstract void setupComponent();

    protected abstract BasePersenter getPersenter();
}