package frogermcs.io.githubclient.persenter.ioc.component;

import dagger.Subcomponent;
import frogermcs.io.githubclient.persenter.ioc.scope.ActivityScope;
import frogermcs.io.githubclient.ui.activity.RepositoriesListActivity;
import frogermcs.io.githubclient.persenter.ioc.module.RepositoriesListActivityModule;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
@ActivityScope
@Subcomponent(
        modules = RepositoriesListActivityModule.class
)
public interface RepositoriesListActivityComponent {

    RepositoriesListActivity inject(RepositoriesListActivity repositoriesListActivity);

}