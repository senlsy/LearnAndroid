package frogermcs.io.githubclient;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import frogermcs.io.githubclient.data.ioc.component.UserComponent;
import frogermcs.io.githubclient.data.ioc.module.UserModule;
import frogermcs.io.githubclient.data.model.User;
import frogermcs.io.githubclient.utils.Test;
import timber.log.Timber;

/**
 * Created by Miroslaw Stanek on 22.04.15.
 */
public class GithubClientApplication extends Application {

    private AppComponent appComponent;
    private UserComponent userComponent;

    @Inject
    Test mTest;//被注入appComponent实例提供的Test实例对象

    /**
     * 1、参数test被注入appComponent实例提供的Test实例对象<br/>
     * 2、该函数在appComponent.inject(this)的时候自动执行
     */
    @Inject
    public void startTest(Test test) {
        test.start();
    }

    public static GithubClientApplication get(Context context) {
        return (GithubClientApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        initAppComponent();
    }

    private void initAppComponent() {

        //类仅仅是描述，一切的一切都还是需要创建类的实例对象才能被使用。
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))//创建组件实例
                .build();

        //执行注入函数之后，被使用到的组件才会生成，并注入到（@Inject 变量 |@Inject 函数）的地方
        appComponent.inject(this);
        mTest.start();

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public UserComponent createUserComponent(User user) {
        userComponent = appComponent.plus(new UserModule(user));
        return userComponent;
    }

    public void releaseUserComponent() {
        userComponent = null;
    }


    public UserComponent getUserComponent() {
        return userComponent;
    }

}