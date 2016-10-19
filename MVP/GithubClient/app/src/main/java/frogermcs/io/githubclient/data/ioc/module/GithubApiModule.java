package frogermcs.io.githubclient.data.ioc.module;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import frogermcs.io.githubclient.BuildConfig;
import frogermcs.io.githubclient.R;
import frogermcs.io.githubclient.data.api.GithubApiService;
import frogermcs.io.githubclient.data.api.UserManager;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Miroslaw Stanek on 22.04.15.
 */
@Module
public class GithubApiModule {


    /**
     * @return 提供okHttpClient组件给归属的component实例
     */
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    /**
     * @param application  参数实例对象是从该Moude归属的component实例中找
     * @param okHttpClient 参数实例对象从该Moude归属的component实例中找
     */
    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(Application application, OkHttpClient okHttpClient) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setClient(new OkClient(okHttpClient))
                .setEndpoint(application.getString(R.string.endpoint));

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    public GithubApiService provideGithubApiService(RestAdapter restAdapter) {
        return restAdapter.create(GithubApiService.class);
    }

    @Provides
    @Singleton
    public UserManager provideUserManager(GithubApiService githubApiService) {
        System.out.println("==BBB");
        return new UserManager(githubApiService);
    }
}
