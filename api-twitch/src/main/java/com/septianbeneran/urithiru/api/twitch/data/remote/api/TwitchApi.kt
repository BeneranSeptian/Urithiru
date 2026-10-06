package com.septianbeneran.urithiru.api.twitch.data.remote.api

import com.septianbeneran.urithiru.api.twitch.data.remote.dto.TwitchOAuthTokenResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface TwitchApi {
    @POST
    suspend fun postTwitchToken(
        @Url url: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "client_credentials"
    ): Response<TwitchOAuthTokenResponse>
}