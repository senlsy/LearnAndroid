package frogermcs.io.githubclient.persenter.ioc.module;

import dagger.Module;
import dagger.Provides;
import frogermcs.io.githubclient.data.api.RepositoriesManager;
import frogermcs.io.githubclient.persenter.ioc.scope.ActivityScope;
import frogermcs.io.githubclient.persenter.RepositoriesListActivityPresenter;
import frogermcs.io.githubclient.pv_interface.RepositoriesListConstruct;
import frogermcs.io.githubclient.ui.activity.RepositoriesListActivity;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
@Module
public class RepositoriesListActivityModule {
    private RepositoriesListActivity repositoriesListActivity;

    public RepositoriesListActivityModule(RepositoriesListActivity repositoriesListActivity) {
        this.repositoriesListActivity = repositoriesListActivity;
    }

    @Provides
    @ActivityScope
    RepositoriesListActivity provideRepositoriesListActivity() {
        return repositoriesListActivity;
    }

    @Provides
    @ActivityScope
    RepositoriesListConstruct.RepositoriesListPersenter provideRepositoriesListActivityPresenter(RepositoriesManager repositoriesManager) {
        return new RepositoriesListActivityPresenter(repositoriesManager);
    }
}