package com.septianbeneran.urithiru.api.dummyjson.di

import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCache
import com.septianbeneran.urithiru.api.dummyjson.data.local.AuthCacheImpl
import com.septianbeneran.urithiru.api.dummyjson.data.remote.api.AuthApi
import com.septianbeneran.urithiru.api.dummyjson.data.remote.api.UserApi
import com.septianbeneran.urithiru.api.dummyjson.data.remote.interceptor.AuthInterceptor
import com.septianbeneran.urithiru.api.dummyjson.data.remote.interceptor.TokenAuthenticator
import com.septianbeneran.urithiru.api.dummyjson.data.remote.service.AuthRemoteDataSource
import com.septianbeneran.urithiru.api.dummyjson.data.remote.service.AuthRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.DummyJsonNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DummyJsonModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthCache(impl: AuthCacheImpl): AuthCache

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(
            @DummyJsonNetwork retrofit: Retrofit
        ): AuthApi = retrofit.create(AuthApi::class.java)

        /** The same Retrofit, with a client that sends the access token and refreshes it on a 401. */
        @Provides
        @Singleton
        fun provideUserApi(
            @DummyJsonNetwork retrofit: Retrofit,
            okHttpClient: OkHttpClient,
            authInterceptor: AuthInterceptor,
            tokenAuthenticator: TokenAuthenticator
        ): UserApi {
            val authenticatedClient = okHttpClient.newBuilder()
                .addInterceptor(authInterceptor)
                .authenticator(tokenAuthenticator)
                .build()
            return retrofit.newBuilder()
                .client(authenticatedClient)
                .build()
                .create(UserApi::class.java)
        }
    }
}
