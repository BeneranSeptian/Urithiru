package com.septianbeneran.urithiru.api.twitch.util

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.RefreshTokenInterface
import com.septianbeneran.urithiru.api.twitch.repository.TwitchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshTokenImpl @Inject constructor(
    private val repository: TwitchRepository
) : RefreshTokenInterface {
    override fun postRereshToken(clientId: String, clientSecret: String): Flow<ApiResult<Any>> =
        repository.postTwitchToken(clientId, clientSecret)
}