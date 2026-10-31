package com.septianbeneran.urithiru.api.dummyjson.data.remote.service

import com.septianbeneran.urithiru.api.dummyjson.BuildConfig.AUTH_LOGIN_V1
import com.septianbeneran.urithiru.api.dummyjson.BuildConfig.AUTH_ME_V1
import com.septianbeneran.urithiru.api.dummyjson.BuildConfig.AUTH_REFRESH_V1
import com.septianbeneran.urithiru.api.dummyjson.data.remote.api.AuthApi
import com.septianbeneran.urithiru.api.dummyjson.data.remote.api.UserApi
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginRequest
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.RefreshTokenRequest
import com.septianbeneran.urithiru.core.base.BaseDataSource
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApi,
    private val userApi: UserApi
) : AuthRemoteDataSource, BaseDataSource() {
    override suspend fun login(username: String, password: String) = getResult {
        api.login(AUTH_LOGIN_V1, LoginRequest(username = username, password = password))
    }

    override suspend fun refreshToken(refreshToken: String) = getResult {
        api.refreshToken(AUTH_REFRESH_V1, RefreshTokenRequest(refreshToken = refreshToken))
    }

    override suspend fun getMe() = getResult {
        userApi.getMe(AUTH_ME_V1)
    }
}
