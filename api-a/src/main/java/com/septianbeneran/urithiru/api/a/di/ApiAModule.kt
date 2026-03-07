package com.septianbeneran.urithiru.api.a.di

import com.septianbeneran.urithiru.api.a.data.local.WeaponCache
import com.septianbeneran.urithiru.api.a.data.local.WeaponCacheImpl
import com.septianbeneran.urithiru.api.a.data.remote.api.WeaponApi
import com.septianbeneran.urithiru.api.a.data.remote.service.ApiARemoteDataSource
import com.septianbeneran.urithiru.api.a.data.remote.service.ApiARemoteDataSourceImpl
import com.septianbeneran.urithiru.api.a.data.repository.WeaponRepository
import com.septianbeneran.urithiru.api.a.data.repository.WeaponRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
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
class ApiAModule {

    @Provides
    @Singleton
    fun provideEldenRingApi(
        @EldenRingNetwork retrofit: Retrofit
    ): WeaponApi {
        return retrofit.create(WeaponApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiARemoteDataSource(
        api: WeaponApi
    ): ApiARemoteDataSource = ApiARemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideWeaponCache(
        baseDataStore: BaseDataStore
    ): WeaponCache = WeaponCacheImpl(baseDataStore)

    @Provides
    @Singleton
    fun provideRepository(
        remote: ApiARemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider,
        @ApplicationScope applicationScope: CoroutineScope,
        cache: WeaponCache
    ): WeaponRepository = WeaponRepositoryImpl(remote, dispatcherProvider, applicationScope, cache)
}