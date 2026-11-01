package com.septianbeneran.urithiru.api.twitch.data.local

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import kotlinx.coroutines.flow.Flow

interface TwitchCache {
    suspend fun saveTwitchOAuthToken(oAuthToken: TwitchOAuthToken)

    suspend fun loadTwitchOAuthToken(): Flow<TwitchOAuthToken?>
}