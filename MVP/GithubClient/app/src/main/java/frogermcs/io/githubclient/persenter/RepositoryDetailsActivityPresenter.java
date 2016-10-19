package frogermcs.io.githubclient.persenter;

import frogermcs.io.githubclient.data.model.User;
import frogermcs.io.githubclient.ui.activity.MVP_View;
import frogermcs.io.githubclient.pv_interface.RepositoryDetailsConstruct;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
public class RepositoryDetailsActivityPresenter implements RepositoryDetailsConstruct.RepositoryDetailsPersenter {

    private RepositoryDetailsConstruct.RepositoryDetailsListView mView;
    private User user;

    public RepositoryDetailsActivityPresenter(User user) {
        this.user = user;
    }

    @Override
    public void init() {
        mView.setupUserName(user.login);
    }

    @Override
    public void attachView(MVP_View view) {
        mView = (RepositoryDetailsConstruct.RepositoryDetailsListView) view;
    }

    @Override
    public void detachView(MVP_View view) {
        if (mView == view) {
            mView = null;
        }
    }
}
