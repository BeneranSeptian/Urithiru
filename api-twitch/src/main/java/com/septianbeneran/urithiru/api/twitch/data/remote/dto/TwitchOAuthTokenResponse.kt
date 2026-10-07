package com.septianbeneran.urithiru.api.twitch.data.remote.dto

import kotlinx.serialization.SerialName
import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import kotlinx.serialization.Serializable

@Serializable
data class TwitchOAuthTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("token_type") val tokenType: String
) {
    fun mapToTwitchOAuthToken() = TwitchOAuthToken(
        accessToken = accessToken,
        expiresIn = expiresIn,
        tokenType = tokenType
    )
}
