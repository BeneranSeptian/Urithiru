package com.septianbeneran.urithiru.api.dummyjson.repository

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(username: String, password: String): Flow<ApiResult<AuthSession>>
    fun observeSession(): Flow<AuthSession?>
}
