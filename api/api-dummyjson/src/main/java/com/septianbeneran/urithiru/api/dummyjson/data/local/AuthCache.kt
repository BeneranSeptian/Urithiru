package com.septianbeneran.urithiru.api.dummyjson.data.local

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthCache {
    suspend fun saveSession(session: AuthSession)
    fun loadSession(): Flow<AuthSession?>
    suspend fun clearSession()
    suspend fun setSessionExpired(isExpired: Boolean)
    fun loadSessionExpired(): Flow<Boolean>
}
