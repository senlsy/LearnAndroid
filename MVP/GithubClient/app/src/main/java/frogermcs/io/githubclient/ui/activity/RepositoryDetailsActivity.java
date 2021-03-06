package frogermcs.io.githubclient.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import frogermcs.io.githubclient.GithubClientApplication;
import frogermcs.io.githubclient.R;
import frogermcs.io.githubclient.data.model.Repository;
import frogermcs.io.githubclient.persenter.ioc.module.RepositoryDetailsActivityModule;
import frogermcs.io.githubclient.persenter.BasePersenter;
import frogermcs.io.githubclient.pv_interface.RepositoryDetailsConstruct;
import frogermcs.io.githubclient.utils.AnalyticsManager;


public class RepositoryDetailsActivity extends BaseActivity implements RepositoryDetailsConstruct.RepositoryDetailsListView {
    private static final String ARG_REPOSITORY = "arg_repository";

    @Bind(R.id.tvRepoName)
    TextView tvRepoName;
    @Bind(R.id.tvRepoDetails)
    TextView tvRepoDetails;
    @Bind(R.id.tvUserName)
    TextView tvUserName;

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    RepositoryDetailsConstruct.RepositoryDetailsPersenter presenter;

    private Repository repository;

    @Override
    protected void setupComponent() {
        GithubClientApplication.get(this).getUserComponent()
                .plus(new RepositoryDetailsActivityModule(this))
                .inject(this);

    }

    @Override
    protected BasePersenter getPersenter() {
        return presenter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_details);
        ButterKnife.bind(this);
        analyticsManager.logScreenView(getClass().getName());
        repository = getIntent().getParcelableExtra(ARG_REPOSITORY);
        tvRepoName.setText(repository.name);
        tvRepoDetails.setText(repository.url);
        presenter.init();
    }

    public static void startWithRepository(Repository repository, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, RepositoryDetailsActivity.class);
        intent.putExtra(ARG_REPOSITORY, repository);
        startingActivity.startActivity(intent);
    }


    public void setupUserName(String userName) {
        tvUserName.setText(userName);
    }
}