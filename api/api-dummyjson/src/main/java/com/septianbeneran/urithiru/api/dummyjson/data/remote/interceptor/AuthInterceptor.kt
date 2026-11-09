package com.septianbeneran.urithiru.api.dummyjson.data.remote.interceptor

import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCache
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/** Adds the saved access token to every request made through UserApi. */
@Singleton
class AuthInterceptor @Inject constructor(
    private val cache: AuthCache
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { cache.loadSession().first()?.accessToken }
        val request = chain.request().newBuilder()
            .apply { accessToken?.let { header(AuthHeader.NAME, AuthHeader.bearer(it)) } }
            .build()
        return chain.proceed(request)
    }
}
