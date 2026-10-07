package com.septianbeneran.urithiru.api.a.di

import com.septianbeneran.urithiru.api.a.data.local.WeaponCache
import com.septianbeneran.urithiru.api.a.data.local.WeaponCacheImpl
import com.septianbeneran.urithiru.api.a.data.remote.api.WeaponApi
import com.septianbeneran.urithiru.api.a.data.remote.service.ApiARemoteDataSource
import com.septianbeneran.urithiru.api.a.data.remote.service.ApiARemoteDataSourceImpl
import com.septianbeneran.urithiru.api.a.repository.WeaponRepository
import com.septianbeneran.urithiru.api.a.repository.WeaponRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiAModule {

    @Binds
    @Singleton
    abstract fun bindApiARemoteDataSource(impl: ApiARemoteDataSourceImpl): ApiARemoteDataSource

    @Binds
    @Singleton
    abstract fun bindWeaponCache(impl: WeaponCacheImpl): WeaponCache

    @Binds
    @Singleton
    abstract fun bindWeaponRepository(impl: WeaponRepositoryImpl): WeaponRepository

    companion object {
        @Provides
        @Singleton
        fun provideWeaponApi(
            @EldenRingNetwork retrofit: Retrofit
        ): WeaponApi = retrofit.create(WeaponApi::class.java)
    }
}