package frogermcs.io.githubclient.data.ioc.component;

import dagger.Subcomponent;
import frogermcs.io.githubclient.data.ioc.scope.UserScope;
import frogermcs.io.githubclient.data.ioc.module.UserModule;
import frogermcs.io.githubclient.persenter.ioc.component.RepositoriesListActivityComponent;
import frogermcs.io.githubclient.persenter.ioc.component.RepositoryDetailsActivityComponent;
import frogermcs.io.githubclient.persenter.ioc.module.RepositoriesListActivityModule;
import frogermcs.io.githubclient.persenter.ioc.module.RepositoryDetailsActivityModule;

/**
 * Created by Miroslaw Stanek on 23.06.15.
 */
@UserScope
@Subcomponent(
        modules = {
                UserModule.class
        }
)
public interface UserComponent {

    RepositoriesListActivityComponent plus(RepositoriesListActivityModule module);

    RepositoryDetailsActivityComponent plus(RepositoryDetailsActivityModule module);
}