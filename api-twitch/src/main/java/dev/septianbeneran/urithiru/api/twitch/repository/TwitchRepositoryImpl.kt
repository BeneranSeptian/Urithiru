package dev.septianbeneran.urithiru.api.twitch.repository

import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import dev.septianbeneran.urithiru.api.twitch.data.local.TwitchCache
import dev.septianbeneran.urithiru.api.twitch.data.remote.service.TwitchApiRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TwitchRepositoryImpl @Inject constructor(
    private val remote: TwitchApiRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider,
    override val cache: TwitchCache,
    @ApplicationScope override val applicationScope: CoroutineScope,
) : TwitchRepository, BaseRepository(applicationScope) {
    override fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): Flow<ApiResult<TwitchOAuthToken>> = resultFlow(
        networkCall = { remote.postTwitchToken(clientId, clientSecret) },
        dispatcher = dispatcher
    ).mapToEntity(
        transform = { it?.mapToTwitchOAuthToken() },
        saveResult = {
            it?.let { cache.saveTwitchOAuthToken(it) }
        }
    )

    override fun saveTwitchToken(oAuthToken: TwitchOAuthToken) {
        TODO("Not yet implemented")
    }

    override fun loadTwitchToken(): Flow<TwitchOAuthToken> {
        TODO("Not yet implemented")
    }
}