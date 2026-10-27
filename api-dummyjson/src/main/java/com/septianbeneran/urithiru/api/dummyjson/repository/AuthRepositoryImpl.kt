package com.septianbeneran.urithiru.api.dummyjson.repository

import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCache
import com.septianbeneran.urithiru.api.dummyjson.data.remote.service.AuthRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val cache: AuthCache,
    private val remote: AuthRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider
) : AuthRepository, BaseRepository(applicationScope) {
    override fun login(username: String, password: String) = resultFlow(
        networkCall = { remote.login(username, password) },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { requireNotNull(it).mapToEntity() },
        saveResult = { cache.saveSession(it) }
    )

    override fun observeSession() = cache.loadSession()
}
