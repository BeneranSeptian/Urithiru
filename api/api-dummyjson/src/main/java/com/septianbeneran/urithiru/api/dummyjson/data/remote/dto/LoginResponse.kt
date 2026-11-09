package com.septianbeneran.urithiru.api.dummyjson.data.remote.dto

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.entity.auth.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("image") val image: String,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
) {
    fun mapToEntity() = AuthSession(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = User(
            id = id,
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            image = image
        )
    )
}
