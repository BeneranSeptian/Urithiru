package dev.septianbeneran.urithiru.api.twitch.data.remote.service

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import dev.septianbeneran.urithiru.api.twitch.data.remote.dto.TwitchOAuthTokenResponse

interface TwitchApiRemoteDataSource {
    suspend fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): ApiResult<TwitchOAuthTokenResponse>
}