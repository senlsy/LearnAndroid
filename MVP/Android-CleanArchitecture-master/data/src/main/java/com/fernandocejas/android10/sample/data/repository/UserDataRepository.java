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
package com.fernandocejas.android10.sample.data.repository;

import com.fernandocejas.android10.sample.data.entity.mapper.UserEntityDataMapper;
import com.fernandocejas.android10.sample.data.repository.datasource.UserDataStore;
import com.fernandocejas.android10.sample.data.repository.datasource.UserDataStoreFactory;
import com.fernandocejas.android10.sample.domain.User;
import com.fernandocejas.android10.sample.domain.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * {@link UserRepository} for retrieving user data.
 */
@Singleton
public class UserDataRepository implements UserRepository {

    private final UserDataStoreFactory userDataStoreFactory;//获取User存储的介质，缓存有效是缓存，否则是服务器上的数据
    private final UserEntityDataMapper userEntityDataMapper;//数据转换，将取得的UserEntity转换成界面所需的User

    /**
     * Constructs a {@link UserRepository}.
     *
     * @param dataStoreFactory     A factory to construct different data source implementations.
     * @param userEntityDataMapper {@link UserEntityDataMapper}.
     */
    @Inject
    public UserDataRepository(UserDataStoreFactory dataStoreFactory, UserEntityDataMapper userEntityDataMapper) {
        this.userDataStoreFactory = dataStoreFactory;
        this.userEntityDataMapper = userEntityDataMapper;
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<List<User>> users() {
        //we always get all users from the cloud,用户列表数据没有缓存，所以直接使用服务器上的数据
        final UserDataStore userDataStore = this.userDataStoreFactory.createCloudDataStore();
        return userDataStore.userEntityList()
                .map(userEntities -> this.userEntityDataMapper.transform(userEntities));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<User> user(int userId) {
        //用户详情可能存在缓存，所以简单工厂模式取到的可能是缓存数据，也可能是服务器数据
        final UserDataStore userDataStore = this.userDataStoreFactory.create(userId);
        return userDataStore.userEntityDetails(userId)
                .map(userEntity -> this.userEntityDataMapper.transform(userEntity));
    }

}
