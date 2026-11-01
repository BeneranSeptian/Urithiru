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

    /** True after a rejected refresh token signed the user out, until they sign in again or dismiss it. */
    fun observeSessionExpired(): Flow<Boolean>
    suspend fun dismissSessionExpired()

    /**
     * Gets and saves a new access token after [expiredAccessToken] was rejected. Returns null when there is
     * no session or the refresh failed. A rejected refresh token also signs the user out and marks the session as expired.
     */
    suspend fun refreshAccessToken(expiredAccessToken: String?): String?
}
