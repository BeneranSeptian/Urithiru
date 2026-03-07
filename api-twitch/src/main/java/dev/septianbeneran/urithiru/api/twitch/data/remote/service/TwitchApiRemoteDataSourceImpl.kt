package dev.septianbeneran.urithiru.api.twitch.data.remote.service

import com.septianbeneran.urithiru.core.base.BaseDataSource
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import dev.septianbeneran.urithiru.api.twitch.data.remote.api.TwitchApi
import dev.septianbeneran.urithiru.api.twitch.data.remote.dto.TwitchOAuthTokenResponse
import javax.inject.Inject

class TwitchApiRemoteDataSourceImpl @Inject constructor(
    private val api: TwitchApi
) : TwitchApiRemoteDataSource, BaseDataSource() {
    override suspend fun postTwitchToken(
        clientId: String,
        clientSecret: String
    ): ApiResult<TwitchOAuthTokenResponse> = getResult {
        api.postTwitchToken("oauth2/token", clientId, clientSecret)
    }
}