package com.septianbeneran.urithiru.api.dummyjson.repository

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.entity.auth.UserProfile
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(username: String, password: String): Flow<ApiResult<AuthSession>>
    fun observeSession(): Flow<AuthSession?>
    fun getProfile(): Flow<ApiResult<UserProfile>>
    suspend fun logout()

    /**
     * Gets and saves a new access token after [expiredAccessToken] was rejected. Returns null when there is
     * no session or the refresh failed. A rejected refresh token also signs the user out.
     */
    suspend fun refreshAccessToken(expiredAccessToken: String?): String?
}
