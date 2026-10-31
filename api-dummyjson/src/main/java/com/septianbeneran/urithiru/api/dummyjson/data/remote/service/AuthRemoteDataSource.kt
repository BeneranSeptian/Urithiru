package com.septianbeneran.urithiru.api.dummyjson.data.remote.service

import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginResponse
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.RefreshTokenResponse
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.UserProfileResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface AuthRemoteDataSource {
    suspend fun login(username: String, password: String): ApiResult<LoginResponse>
    suspend fun refreshToken(refreshToken: String): ApiResult<RefreshTokenResponse>
    suspend fun getMe(): ApiResult<UserProfileResponse>
}
