package com.septianbeneran.urithiru.core.entity.twitch

import kotlinx.serialization.Serializable

@Serializable
data class TwitchOAuthToken(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String
)