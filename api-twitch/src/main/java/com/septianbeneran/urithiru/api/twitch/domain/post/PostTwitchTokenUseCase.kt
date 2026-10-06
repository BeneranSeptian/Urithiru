package com.septianbeneran.urithiru.api.twitch.domain.post

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface PostTwitchTokenUseCase {
    operator fun invoke(clientId: String, clientSecret: String): Flow<ApiResult<TwitchOAuthToken>>
}