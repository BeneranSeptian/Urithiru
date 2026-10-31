package com.septianbeneran.urithiru.api.dummyjson.repository

import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCache
import com.septianbeneran.urithiru.api.dummyjson.data.remote.service.AuthRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val cache: AuthCache,
    private val remote: AuthRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider
) : AuthRepository, BaseRepository(applicationScope) {
    private val refreshMutex = Mutex()

    override fun login(username: String, password: String) = resultFlow(
        networkCall = { remote.login(username, password) },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { requireNotNull(it).mapToEntity() },
        saveResult = { cache.saveSession(it) }
    )

    override fun observeSession() = cache.loadSession()

    override fun getProfile() = resultFlow(
        networkCall = { remote.getMe() },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { requireNotNull(it).mapToEntity() }
    )

    override suspend fun logout() = cache.clearSession()

    override suspend fun refreshAccessToken(expiredAccessToken: String?): String? = refreshMutex.withLock {
        val session = cache.loadSession().first() ?: return null
        // Another request already refreshed the token while this one waited for the lock
        if (session.accessToken != expiredAccessToken) return session.accessToken

        when (val result = remote.refreshToken(session.refreshToken)) {
            is ApiResult.Success -> {
                val tokens = result.data ?: return null
                cache.saveSession(session.copy(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken))
                tokens.accessToken
            }
            is ApiResult.Error -> {
                if (result.error is AppError.Unauthorized || result.error is AppError.Http) cache.clearSession()
                null
            }
            is ApiResult.Loading -> null
        }
    }
}
