package frogermcs.io.githubclient.persenter;

import frogermcs.io.githubclient.HeavyLibraryWrapper;
import frogermcs.io.githubclient.data.api.UserManager;
import frogermcs.io.githubclient.data.model.User;
import frogermcs.io.githubclient.ui.activity.MVP_View;
import frogermcs.io.githubclient.pv_interface.SplashActivityConstruct;
import frogermcs.io.githubclient.utils.SimpleObserver;
import frogermcs.io.githubclient.utils.Validator;

/**
 * Created by Miroslaw Stanek on 23.04.15.
 */
public class SplashActivityPresenter implements SplashActivityConstruct.SplashPersenter {
    private String username;

    private SplashActivityConstruct.SplashView mView;
    private Validator validator;
    private UserManager userManager;
    private HeavyLibraryWrapper heavyLibraryWrapper;


    public SplashActivityPresenter(Validator validator, UserManager userManager, HeavyLibraryWrapper heavyLibraryWrapper) {
        this.validator = validator;
        this.userManager = userManager;
        this.heavyLibraryWrapper = heavyLibraryWrapper;

        //This calls should be delivered to ExternalLibrary right after it will be initialized
        this.heavyLibraryWrapper.callMethod();
        this.heavyLibraryWrapper.callMethod();
        this.heavyLibraryWrapper.callMethod();
        this.heavyLibraryWrapper.callMethod();
    }

    @Override
    public void onShowRepositoriesClick() {
        if (validator.validUsername(username)) {
            mView.showLoading(true);
            userManager.getUser(username).subscribe(new SimpleObserver<User>() {
                @Override
                public void onNext(User user) {
                    mView.showLoading(false);
                    mView.showRepositoriesListForUser(user);
                }

                @Override
                public void onError(Throwable e) {
                    mView.showLoading(false);
                    mView.showValidationError();
                }
            });
        } else {
            mView.showValidationError();
        }
    }

    @Override
    public void attachView(MVP_View view) {
        mView = (SplashActivityConstruct.SplashView) view;
    }

    @Override
    public void detachView(MVP_View view) {
        if (mView == view) {
            mView = null;
        }
    }


    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    //==========================
    //
    //==========================


}
