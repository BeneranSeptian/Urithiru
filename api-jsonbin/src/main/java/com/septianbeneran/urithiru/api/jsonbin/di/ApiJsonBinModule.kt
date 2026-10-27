package com.septianbeneran.urithiru.api.jsonbin.di

import com.septianbeneran.urithiru.api.jsonbin.data.local.JsonBinCache
import com.septianbeneran.urithiru.api.jsonbin.data.local.JsonBinCacheImpl
import com.septianbeneran.urithiru.api.jsonbin.data.remote.api.ApiJsonBin
import com.septianbeneran.urithiru.api.jsonbin.data.remote.service.ApiJsonBinRemoteDataSource
import com.septianbeneran.urithiru.api.jsonbin.data.remote.service.ApiJsonBinRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.jsonbin.repository.ApiJsonBinRepository
import com.septianbeneran.urithiru.api.jsonbin.repository.ApiJsonBinRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.JsonBinNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiJsonBinModule {

    @Binds
    @Singleton
    abstract fun bindApiJsonBinRemoteDataSource(impl: ApiJsonBinRemoteDataSourceImpl): ApiJsonBinRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindJsonBinCache(impl: JsonBinCacheImpl): JsonBinCache

    @Binds
    @Singleton
    abstract fun bindApiJsonBinRepository(impl: ApiJsonBinRepositoryImpl): ApiJsonBinRepository

    companion object {
        @Provides
        @Singleton
        fun provideApiJsonBin(
            @JsonBinNetwork retrofit: Retrofit
        ): ApiJsonBin = retrofit.create(ApiJsonBin::class.java)
    }
}