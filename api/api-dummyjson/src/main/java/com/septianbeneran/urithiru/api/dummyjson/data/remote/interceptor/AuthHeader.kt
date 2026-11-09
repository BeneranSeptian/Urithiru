package com.septianbeneran.urithiru.api.dummyjson.data.remote.interceptor

internal object AuthHeader {
    const val NAME = "Authorization"
    const val BEARER_PREFIX = "Bearer "

    fun bearer(accessToken: String) = "$BEARER_PREFIX$accessToken"
}
