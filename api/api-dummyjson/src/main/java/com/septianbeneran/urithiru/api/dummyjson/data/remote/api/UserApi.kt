package com.septianbeneran.urithiru.api.dummyjson.data.remote.api

import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/** Endpoints that need the signed-in user's access token. Its client adds the token and refreshes it on a 401. */
interface UserApi {
    @GET
    suspend fun getMe(
        @Url url: String
    ): Response<UserProfileResponse>
}
