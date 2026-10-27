package com.septianbeneran.urithiru.api.eldenring.di

import com.septianbeneran.urithiru.api.eldenring.data.local.WeaponCache
import com.septianbeneran.urithiru.api.eldenring.data.local.WeaponCacheImpl
import com.septianbeneran.urithiru.api.eldenring.data.remote.api.WeaponApi
import com.septianbeneran.urithiru.api.eldenring.data.remote.service.WeaponRemoteDataSource
import com.septianbeneran.urithiru.api.eldenring.data.remote.service.WeaponRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.eldenring.repository.WeaponRepository
import com.septianbeneran.urithiru.api.eldenring.repository.WeaponRepositoryImpl
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
abstract class EldenRingModule {

    @Binds
    @Singleton
    abstract fun bindApiARemoteDataSource(impl: WeaponRemoteDataSourceImpl): WeaponRemoteDataSource

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