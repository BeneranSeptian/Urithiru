package com.septianbeneran.urithiru.core.util

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface RefreshTokenInterface {
    fun postRefreshToken(
        clientId: String,
        clientSecret: String
    ): Flow<ApiResult<Any>>
}