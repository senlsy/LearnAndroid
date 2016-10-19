/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.android10.sample.presentation.internal.di.modules;

import android.content.Context;

import com.fernandocejas.android10.sample.data.cache.UserCache;
import com.fernandocejas.android10.sample.data.cache.UserCacheImpl;
import com.fernandocejas.android10.sample.data.executor.JobExecutor;
import com.fernandocejas.android10.sample.data.repository.UserDataRepository;
import com.fernandocejas.android10.sample.domain.executor.PostExecutionThread;
import com.fernandocejas.android10.sample.domain.executor.ThreadExecutor;
import com.fernandocejas.android10.sample.domain.repository.UserRepository;
import com.fernandocejas.android10.sample.presentation.AndroidApplication;
import com.fernandocejas.android10.sample.presentation.UIThread;
import com.fernandocejas.android10.sample.presentation.navigation.Navigator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {

    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    /**
     * 提供一个Application的context上下文
     *
     * @return
     */
    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    /**
     * 提供一个导航对象，Activity的跳转都通过该对象
     *
     * @return
     */
    @Provides
    @Singleton
    Navigator provideNavigator() {
        return new Navigator();
    }

    /**
     * 提供一个线程执行者
     *
     * @param jobExecutor
     * @return
     */
    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    /**
     * 提供一个android rxjava转主线程的Scheduler
     *
     * @param uiThread
     * @return
     */
    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    /**
     * 提供一个User的缓存
     *
     * @param userCache
     * @return
     */
    @Provides
    @Singleton
    UserCache provideUserCache(UserCacheImpl userCache) {
        return userCache;
    }


    /**
     * 提供一个获取userEntity的仓库
     *
     * @param userDataRepository
     * @return
     */
    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }
}
