package dev.septianbeneran.urithiru.api.twitch.data.dto

import com.google.gson.annotations.SerializedName
import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import kotlinx.serialization.Serializable

@Serializable
data class TwitchOAuthTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String
) {
    fun mapToTwitchOAuthToken() = TwitchOAuthToken(
        accessToken = accessToken,
        expiresIn = expiresIn,
        tokenType = tokenType
    )
}
