package dev.septianbeneran.urithiru.api.twitch.data.service

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import dev.septianbeneran.urithiru.api.twitch.data.dto.TwitchOAuthTokenResponse

interface TwitchApiRemoteDataSource {
    suspend fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): ApiResult<TwitchOAuthTokenResponse>
}