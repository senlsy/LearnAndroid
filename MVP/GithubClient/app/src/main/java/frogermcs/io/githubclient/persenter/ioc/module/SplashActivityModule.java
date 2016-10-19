package frogermcs.io.githubclient.persenter.ioc.module;

import dagger.Module;
import dagger.Provides;
import frogermcs.io.githubclient.HeavyLibraryWrapper;
import frogermcs.io.githubclient.data.api.UserManager;
import frogermcs.io.githubclient.persenter.ioc.scope.ActivityScope;
import frogermcs.io.githubclient.pv_interface.SplashActivityConstruct;
import frogermcs.io.githubclient.ui.activity.SplashActivity;
import frogermcs.io.githubclient.persenter.SplashActivityPresenter;
import frogermcs.io.githubclient.utils.Validator;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
@Module
public class SplashActivityModule {
    private SplashActivity splashActivity;

    public SplashActivityModule(SplashActivity splashActivity) {
        this.splashActivity = splashActivity;
    }

    @Provides
    @ActivityScope
    SplashActivity provideSplashActivity() {
        return splashActivity;
    }

    @Provides
    @ActivityScope
    SplashActivityConstruct.SplashPersenter provideSplashActivityPresenter(Validator validator, UserManager userManager, HeavyLibraryWrapper heavyLibraryWrapper) {
        return new SplashActivityPresenter(validator, userManager, heavyLibraryWrapper);
    }
}