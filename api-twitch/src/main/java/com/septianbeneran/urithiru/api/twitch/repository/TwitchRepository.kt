package com.septianbeneran.urithiru.api.twitch.repository

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.api.twitch.data.local.TwitchCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface TwitchRepository {
    val cache: TwitchCache
    val applicationScope: CoroutineScope

    fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): Flow<ApiResult<TwitchOAuthToken>>

    fun saveTwitchToken(
        oAuthToken: TwitchOAuthToken
    )

    fun loadTwitchToken(): Flow<TwitchOAuthToken>
}