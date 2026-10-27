package com.septianbeneran.urithiru.api.dummyjson.data.local

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import javax.inject.Inject

class AuthCacheImpl @Inject constructor(
    private val baseDataStore: BaseDataStore
) : AuthCache {
    override suspend fun saveSession(session: AuthSession) {
        baseDataStore.saveObject(AUTH_SESSION_KEY, session)
    }

    override fun loadSession() = baseDataStore.readObject<AuthSession>(AUTH_SESSION_KEY)

    private companion object {
        const val AUTH_SESSION_KEY = "AUTH_SESSION"
    }
}
