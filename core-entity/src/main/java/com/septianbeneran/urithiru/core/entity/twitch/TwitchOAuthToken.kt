package com.septianbeneran.urithiru.core.entity.twitch

data class TwitchOAuthToken(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String
)