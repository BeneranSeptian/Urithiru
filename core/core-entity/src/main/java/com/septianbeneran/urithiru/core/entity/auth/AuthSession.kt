package com.septianbeneran.urithiru.core.entity.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
