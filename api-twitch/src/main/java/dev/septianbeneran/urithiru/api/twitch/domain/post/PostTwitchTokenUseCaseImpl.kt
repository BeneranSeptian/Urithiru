package dev.septianbeneran.urithiru.api.twitch.domain.post

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import dev.septianbeneran.urithiru.api.twitch.repository.TwitchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostTwitchTokenUseCaseImpl @Inject constructor(
    private val repository: TwitchRepository
) : PostTwitchTokenUseCase {
    override fun invoke(clientId: String, clientSecret: String): Flow<ApiResult<TwitchOAuthToken>> =
        repository.postTwitchToken(clientId, clientSecret)
}