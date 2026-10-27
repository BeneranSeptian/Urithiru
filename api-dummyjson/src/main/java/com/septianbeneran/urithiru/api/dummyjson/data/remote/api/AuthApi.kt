package com.septianbeneran.urithiru.api.dummyjson.data.remote.api

import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginRequest
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthApi {
    @POST
    suspend fun login(
        @Url url: String,
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
