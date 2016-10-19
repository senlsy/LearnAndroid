package frogermcs.io.githubclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import frogermcs.io.githubclient.GithubClientApplication;
import frogermcs.io.githubclient.R;
import frogermcs.io.githubclient.data.model.User;
import frogermcs.io.githubclient.persenter.ioc.component.SplashActivityComponent;
import frogermcs.io.githubclient.persenter.ioc.module.SplashActivityModule;
import frogermcs.io.githubclient.persenter.BasePersenter;
import frogermcs.io.githubclient.pv_interface.SplashActivityConstruct;
import frogermcs.io.githubclient.utils.AnalyticsManager;
import rx.Subscription;
import rx.functions.Action1;


public class SplashActivity extends BaseActivity implements SplashActivityConstruct.SplashView {

    @Bind(R.id.etUsername)
    EditText etUsername;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnShowRepositories)
    Button btnShowRepositories;

    //These references will be satisfied by 'SplashActivityComponent.inject(this)' method
    @Inject
    SplashActivityConstruct.SplashPersenter presenter;

    @Inject
    AnalyticsManager analyticsManager;

    private Subscription textChangeSubscription;

    //Local dependencies graph is constructed here
    @Override
    protected void setupComponent() {
        //Uncomment those lines do measure dependencies creation time
//        Debug.startMethodTracing("SplashTrace");
        SplashActivityComponent component = GithubClientApplication.get(this)
                .getAppComponent().plus(new SplashActivityModule(this));
        component.inject(this);
//        Debug.stopMethodTracing();

    }

    @Override
    protected BasePersenter getPersenter() {
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        analyticsManager.logScreenView(getClass().getName());

        textChangeSubscription = RxTextView.textChangeEvents(etUsername).subscribe(new Action1<TextViewTextChangeEvent>() {
            @Override
            public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
                presenter.setUsername(textViewTextChangeEvent.text().toString());
                etUsername.setError(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textChangeSubscription.unsubscribe();
    }

    @OnClick(R.id.btnShowRepositories)
    public void onShowRepositoriesClick() {
        presenter.onShowRepositoriesClick();
    }

    //====================
    //pv接口协议
    //====================

    @Override
    public void showRepositoriesListForUser(User user) {
        GithubClientApplication.get(this).createUserComponent(user);
        startActivity(new Intent(this, RepositoriesListActivity.class));
    }

    @Override
    public void showValidationError() {
        etUsername.setError("Validation error");
    }

    @Override
    public void showLoading(boolean loading) {
        btnShowRepositories.setVisibility(loading ? View.GONE : View.VISIBLE);
        pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
