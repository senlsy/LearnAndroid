package frogermcs.io.githubclient.persenter;

import com.google.common.collect.ImmutableList;

import frogermcs.io.githubclient.data.api.RepositoriesManager;
import frogermcs.io.githubclient.data.model.Repository;
import frogermcs.io.githubclient.ui.activity.MVP_View;
import frogermcs.io.githubclient.pv_interface.RepositoriesListConstruct;
import frogermcs.io.githubclient.utils.SimpleObserver;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
public class RepositoriesListActivityPresenter implements RepositoriesListConstruct.RepositoriesListPersenter {
    private RepositoriesListConstruct.RepositoriesListView mView;
    private RepositoriesManager repositoriesManager;

    public RepositoriesListActivityPresenter(RepositoriesManager repositoriesManager) {
        this.repositoriesManager = repositoriesManager;
    }

    public void loadRepositories() {
        mView.showLoading(true);
        repositoriesManager.getUsersRepositories().subscribe(new SimpleObserver<ImmutableList<Repository>>() {
            @Override
            public void onNext(ImmutableList<Repository> repositories) {
                mView.showLoading(false);
                mView.setRepositories(repositories);
            }

            @Override
            public void onError(Throwable e) {
                mView.showLoading(false);
            }
        });
    }

    @Override
    public void attachView(MVP_View view) {
        mView = (RepositoriesListConstruct.RepositoriesListView) view;
    }

    @Override
    public void detachView(MVP_View view) {
        if (mView == view) {
            mView = null;
        }
    }
}
