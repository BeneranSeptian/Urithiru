package com.septianbeneran.urithiru.api.twitch.di

import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.annotation.TwitchNetwork
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.septianbeneran.urithiru.api.twitch.data.local.TwitchCache
import com.septianbeneran.urithiru.api.twitch.data.local.TwitchCacheImpl
import com.septianbeneran.urithiru.api.twitch.data.remote.api.TwitchApi
import com.septianbeneran.urithiru.api.twitch.data.remote.service.TwitchApiRemoteDataSource
import com.septianbeneran.urithiru.api.twitch.data.remote.service.TwitchApiRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.twitch.repository.TwitchRepository
import com.septianbeneran.urithiru.api.twitch.repository.TwitchRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiTwitchModule {

    @Provides
    @Singleton
    fun provideTwitchApi(
        @TwitchNetwork retrofit: Retrofit
    ): TwitchApi {
        return retrofit.create(TwitchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiTwitchRemoteDataSource(
        api: TwitchApi
    ): TwitchApiRemoteDataSource = TwitchApiRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideCache(
        dataStore: BaseDataStore
    ): TwitchCache = TwitchCacheImpl(dataStore)

    @Provides
    @Singleton
    fun provideRepository(
        remote: TwitchApiRemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider,
        @ApplicationScope applicationScope: CoroutineScope,
        cache: TwitchCache
    ): TwitchRepository = TwitchRepositoryImpl(remote, dispatcherProvider, cache, applicationScope)
}