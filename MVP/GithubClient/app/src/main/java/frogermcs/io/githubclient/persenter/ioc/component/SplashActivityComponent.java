package frogermcs.io.githubclient.persenter.ioc.component;

import dagger.Subcomponent;
import frogermcs.io.githubclient.persenter.ioc.scope.ActivityScope;
import frogermcs.io.githubclient.ui.activity.SplashActivity;
import frogermcs.io.githubclient.persenter.ioc.module.SplashActivityModule;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
@ActivityScope
@Subcomponent(modules = SplashActivityModule.class
)
public interface SplashActivityComponent {

    SplashActivity inject(SplashActivity splashActivity);

}