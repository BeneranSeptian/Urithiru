package com.septianbeneran.urithiru.api.dummyjson.data.remote.service

import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface AuthRemoteDataSource {
    suspend fun login(username: String, password: String): ApiResult<LoginResponse>
}
