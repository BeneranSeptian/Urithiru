package com.septianbeneran.urithiru.api.dummyjson.data.remote.interceptor

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp calls this when a UserApi request gets a 401. It refreshes the access token and retries the
 * request once. [AuthRepository] is [Lazy] because the repository itself depends on UserApi.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val repository: Lazy<AuthRepository>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null

        val expiredAccessToken = response.request.header(AuthHeader.NAME)?.removePrefix(AuthHeader.BEARER_PREFIX)
        val newAccessToken = runBlocking { repository.get().refreshAccessToken(expiredAccessToken) } ?: return null

        return response.request.newBuilder()
            .header(AuthHeader.NAME, AuthHeader.bearer(newAccessToken))
            .build()
    }
}
