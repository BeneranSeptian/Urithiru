package com.septianbeneran.urithiru.api.dummyjson.data.local

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthCacheImpl @Inject constructor(
    private val baseDataStore: BaseDataStore
) : AuthCache {
    override suspend fun saveSession(session: AuthSession) {
        baseDataStore.saveObject(AUTH_SESSION_KEY, session)
    }

    override fun loadSession() = baseDataStore.readObject<AuthSession>(AUTH_SESSION_KEY)

    override suspend fun clearSession() {
        baseDataStore.remove(AUTH_SESSION_KEY)
    }

    override suspend fun setSessionExpired(isExpired: Boolean) {
        baseDataStore.saveBoolean(SESSION_EXPIRED_KEY, isExpired)
    }

    override fun loadSessionExpired(): Flow<Boolean> =
        baseDataStore.readBoolean(SESSION_EXPIRED_KEY).map { it ?: false }

    private companion object {
        const val AUTH_SESSION_KEY = "AUTH_SESSION"
        const val SESSION_EXPIRED_KEY = "SESSION_EXPIRED"
    }
}
