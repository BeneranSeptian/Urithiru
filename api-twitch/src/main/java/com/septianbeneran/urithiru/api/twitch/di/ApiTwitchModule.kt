package com.septianbeneran.urithiru.api.twitch.di

import com.septianbeneran.urithiru.api.twitch.data.local.TwitchCache
import com.septianbeneran.urithiru.api.twitch.data.local.TwitchCacheImpl
import com.septianbeneran.urithiru.api.twitch.data.remote.api.TwitchApi
import com.septianbeneran.urithiru.api.twitch.data.remote.service.TwitchApiRemoteDataSource
import com.septianbeneran.urithiru.api.twitch.data.remote.service.TwitchApiRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.twitch.repository.TwitchRepository
import com.septianbeneran.urithiru.api.twitch.repository.TwitchRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.TwitchNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiTwitchModule {

    @Binds
    @Singleton
    abstract fun bindTwitchApiRemoteDataSource(impl: TwitchApiRemoteDataSourceImpl): TwitchApiRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTwitchCache(impl: TwitchCacheImpl): TwitchCache

    @Binds
    @Singleton
    abstract fun bindTwitchRepository(impl: TwitchRepositoryImpl): TwitchRepository

    companion object {
        @Provides
        @Singleton
        fun provideTwitchApi(
            @TwitchNetwork retrofit: Retrofit
        ): TwitchApi = retrofit.create(TwitchApi::class.java)
    }
}