package com.septianbeneran.urithiru.api.dummyjson.repository

import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCache
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.LoginResponse
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.RefreshTokenResponse
import com.septianbeneran.urithiru.api.dummyjson.data.remote.dto.UserProfileResponse
import com.septianbeneran.urithiru.api.dummyjson.data.remote.service.AuthRemoteDataSource
import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.entity.auth.User
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    private val session = AuthSession(
        accessToken = "old-access",
        refreshToken = "old-refresh",
        user = User(id = 1, username = "emilys", email = "emily@x.com", firstName = "Emily", lastName = "Johnson", image = "")
    )
    private val cache = FakeAuthCache(session)
    private val remote = FakeRemote()
    private val repository = AuthRepositoryImpl(
        cache = cache,
        remote = remote,
        dispatcher = object : CoroutineDispatcherProvider {}
    )

    @Test
    fun aRejectedRefreshTokenSignsOutAndMarksTheSessionExpired() = runBlocking {
        remote.refreshResult = ApiResult.Error(AppError.Unauthorized)

        val token = repository.refreshAccessToken("old-access")

        assertNull(token)
        assertNull(cache.session.value)
        assertTrue(repository.observeSessionExpired().first())
    }

    @Test
    fun aSuccessfulRefreshSavesTheNewTokensWithoutExpiringTheSession() = runBlocking {
        remote.refreshResult = ApiResult.Success(RefreshTokenResponse(accessToken = "new-access", refreshToken = "new-refresh"))

        val token = repository.refreshAccessToken("old-access")

        assertEquals("new-access", token)
        assertEquals("new-refresh", cache.session.value?.refreshToken)
        assertFalse(repository.observeSessionExpired().first())
    }

    @Test
    fun aNetworkFailureKeepsTheSessionAndDoesNotExpireIt() = runBlocking {
        remote.refreshResult = ApiResult.Error(AppError.NoInternet)

        repository.refreshAccessToken("old-access")

        assertEquals(session, cache.session.value)
        assertFalse(repository.observeSessionExpired().first())
    }

    @Test
    fun dismissingTheExpiredSessionClearsTheFlag() = runBlocking {
        remote.refreshResult = ApiResult.Error(AppError.Unauthorized)
        repository.refreshAccessToken("old-access")

        repository.dismissSessionExpired()

        assertFalse(repository.observeSessionExpired().first())
    }

    private class FakeAuthCache(initial: AuthSession?) : AuthCache {
        val session = MutableStateFlow(initial)
        private val expired = MutableStateFlow(false)

        override suspend fun saveSession(session: AuthSession) {
            this.session.value = session
        }

        override fun loadSession(): Flow<AuthSession?> = session

        override suspend fun clearSession() {
            session.value = null
        }

        override suspend fun setSessionExpired(isExpired: Boolean) {
            expired.value = isExpired
        }

        override fun loadSessionExpired(): Flow<Boolean> = expired
    }

    private class FakeRemote : AuthRemoteDataSource {
        var refreshResult: ApiResult<RefreshTokenResponse> = ApiResult.Error(AppError.NoInternet)

        override suspend fun login(username: String, password: String): ApiResult<LoginResponse> =
            error("Not used")

        override suspend fun refreshToken(refreshToken: String): ApiResult<RefreshTokenResponse> = refreshResult

        override suspend fun getMe(): ApiResult<UserProfileResponse> = error("Not used")
    }
}
