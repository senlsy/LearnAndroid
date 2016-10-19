package uk.co.ribot.androidboilerplate.injection.module;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.ribot.androidboilerplate.data.remote.RibotsService;
import uk.co.ribot.androidboilerplate.injection.ApplicationContext;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {

    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    /**
     * 提供Applicaion实例
     *
     * @return
     */
    @Provides
    Application provideApplication() {
        return mApplication;
    }

    /**
     * 提供Application的context上下文
     *
     * @return
     */
    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    /**
     * 提供一个单例EventBus
     *
     * @return
     */
    @Provides
    @Singleton
    Bus provideEventBus() {
        return new Bus();
    }

    /**
     * 提供一个网络请求对象
     *
     * @return
     */
    @Provides
    @Singleton
    RibotsService provideRibotsService() {
        return RibotsService.Creator.newRibotsService();
    }

}
