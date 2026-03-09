package com.septianbeneran.urithiru.api.b.di

import com.septianbeneran.urithiru.api.b.data.local.JsonBinCache
import com.septianbeneran.urithiru.api.b.data.local.JsonBinCacheImpl
import com.septianbeneran.urithiru.api.b.data.remote.api.ApiJsonBin
import com.septianbeneran.urithiru.api.b.data.remote.service.ApiJsonBinRemoteDataSource
import com.septianbeneran.urithiru.api.b.data.remote.service.ApiJsonBinRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.b.repository.ApiJsonBinRepository
import com.septianbeneran.urithiru.api.b.repository.ApiJsonBinRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.annotation.JsonBinNetwork
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiJsonBinModule {
    @Provides
    @Singleton
    fun provideApiJsonBin(
        @JsonBinNetwork retrofit: Retrofit
    ): ApiJsonBin {
        return retrofit.create(ApiJsonBin::class.java)
    }

    @Provides
    @Singleton
    fun provideApiJsonBinRemoteDataSource(
        api: ApiJsonBin
    ): ApiJsonBinRemoteDataSource = ApiJsonBinRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideWeaponCache(
        baseDataStore: BaseDataStore
    ): JsonBinCache = JsonBinCacheImpl(baseDataStore)

    @Provides
    @Singleton
    fun provideRepository(
        remote: ApiJsonBinRemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider,
        cache: JsonBinCache,
        @ApplicationScope applicationScope: CoroutineScope
    ): ApiJsonBinRepository =
        ApiJsonBinRepositoryImpl(applicationScope, cache, remote, dispatcherProvider)
}