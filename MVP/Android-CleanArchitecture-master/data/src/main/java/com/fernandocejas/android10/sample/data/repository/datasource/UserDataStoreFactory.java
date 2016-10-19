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
package com.fernandocejas.android10.sample.data.repository.datasource;

import android.content.Context;

import com.fernandocejas.android10.sample.data.cache.UserCache;
import com.fernandocejas.android10.sample.data.entity.mapper.UserEntityJsonMapper;
import com.fernandocejas.android10.sample.data.net.RestApi;
import com.fernandocejas.android10.sample.data.net.RestApiImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 简单工厂模式，来提供UserDateStore
 * Factory that creates different implementations of {@link UserDataStore}.
 */
@Singleton
public class UserDataStoreFactory {

    private final Context context;
    private final UserCache userCache;

    @Inject
    public UserDataStoreFactory(Context context, UserCache userCache) {
        if (context == null || userCache == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.context = context.getApplicationContext();
        this.userCache = userCache;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore create(int userId) {

        UserDataStore userDataStore;
        if (!this.userCache.isExpired() && this.userCache.isCached(userId)) {
            userDataStore = createDiskUserDataStore(this.userCache);//硬盘上的数据
        } else {
            userDataStore = createCloudDataStore();//服务器上的数据
        }
        return userDataStore;
    }

    private UserDataStore createDiskUserDataStore(UserCache userCache) {
        return new DiskUserDataStore(userCache);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public UserDataStore createCloudDataStore() {
        UserEntityJsonMapper userEntityJsonMapper = new UserEntityJsonMapper();
        RestApi restApi = new RestApiImpl(this.context, userEntityJsonMapper);
        return new CloudUserDataStore(restApi, this.userCache);
    }
}
