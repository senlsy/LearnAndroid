package frogermcs.io.githubclient;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import frogermcs.io.githubclient.data.ioc.component.UserComponent;
import frogermcs.io.githubclient.data.ioc.module.GithubApiModule;
import frogermcs.io.githubclient.data.ioc.module.UserModule;
import frogermcs.io.githubclient.persenter.ioc.component.SplashActivityComponent;
import frogermcs.io.githubclient.persenter.ioc.module.SplashActivityModule;

/**
 * Created by Miroslaw Stanek on 22.04.15.
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                GithubApiModule.class
        }
)
public interface AppComponent {

    //声明能被注入依赖的类型
    GithubClientApplication inject(GithubClientApplication repositoriesListActivity);//函数名任意,参数跟返回值类型要对

    //派生方式：ComponentA派生一个子ComponentB，子ComponentB可以得到父ComponentA提供的所有依赖
    SplashActivityComponent plus(SplashActivityModule module);

    UserComponent plus(UserModule userModule);//函数名任意，返回类型对了就可以

    //依赖方式：ComponentA依赖另外一个ComponentB，只有ComponentB公开自己的依赖，这些依赖才能提供给ConponentA
    Application getApplication();

    Application getAnalyticsManager();//函数名任意，返回类型对了就可以

}