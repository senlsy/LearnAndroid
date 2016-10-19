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
package com.fernandocejas.android10.sample.data.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fernandocejas.android10.sample.data.entity.UserEntity;
import com.fernandocejas.android10.sample.data.entity.mapper.UserEntityJsonMapper;
import com.fernandocejas.android10.sample.data.exception.NetworkConnectionException;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.net.MalformedURLException;
import java.util.List;

import rx.Observable;

import static com.fernandocejas.frodo.annotation.RxLogObservable.Scope.SCHEDULERS;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    private final Context context;
    private final UserEntityJsonMapper userEntityJsonMapper;

    /**
     * Constructor of the class
     *
     * @param context              {@link android.content.Context}.
     * @param userEntityJsonMapper {@link UserEntityJsonMapper}.
     */
    public RestApiImpl(Context context, UserEntityJsonMapper userEntityJsonMapper) {
        if (context == null || userEntityJsonMapper == null) {
            throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
        }
        this.context = context.getApplicationContext();
        this.userEntityJsonMapper = userEntityJsonMapper;
    }

    @RxLogObservable(SCHEDULERS)
    @Override
    public Observable<List<UserEntity>> userEntityList() {
        return Observable.create(subscriber -> {
            if (isThereInternetConnection()) {
                try {
                    String responseUserEntities = getUserEntitiesFromApi();//发起网络请求，获取用户列表
                    List<UserEntity> userList = userEntityJsonMapper.transformUserEntityCollection(responseUserEntities);
                    if (userList != null) {
                        subscriber.onNext(userList);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(new NetworkConnectionException(e.getCause()));
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    @RxLogObservable(SCHEDULERS)
    @Override
    public Observable<UserEntity> userEntityById(final int userId) {
        return Observable.create(subscriber -> {
            if (isThereInternetConnection()) {
                try {
                    String responseUserDetails = getUserDetailsFromApi(userId);//发送网络请求，获取用户详情
                    UserEntity userDetail = userEntityJsonMapper.transformUserEntity(responseUserDetails);
                    if (userDetail != null) {
                        subscriber.onNext(userDetail);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new NetworkConnectionException());
                    }
                } catch (Exception e) {
                    subscriber.onError(new NetworkConnectionException(e.getCause()));
                }
            } else {
                subscriber.onError(new NetworkConnectionException());
            }
        });
    }

    private String getUserEntitiesFromApi() throws MalformedURLException {
        return ApiConnection.createGET(RestApi.API_URL_GET_USER_LIST).requestSyncCall();//获取user列表，okhttp中的同步请求
    }

    private String getUserDetailsFromApi(int userId) throws MalformedURLException {
        String apiUrl = RestApi.API_URL_GET_USER_DETAILS + userId + ".json";//获取user详情，okhttp中的同步请求
        return ApiConnection.createGET(apiUrl).requestSyncCall();
    }

    /**
     * Checks if the device has any active internet connection.
     *
     * @return true device with internet connection, otherwise false.
     */
    private boolean isThereInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
}
