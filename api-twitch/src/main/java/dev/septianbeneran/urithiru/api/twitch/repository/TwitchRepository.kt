package dev.septianbeneran.urithiru.api.twitch.repository

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface TwitchRepository {

    fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): Flow<ApiResult<TwitchOAuthToken>>
}