package com.septianbeneran.template.api_a.di

import com.septianbeneran.template.api_a.data.remote.api.EldenRingApi
import com.septianbeneran.template.api_a.data.remote.service.ApiARemoteDataSource
import com.septianbeneran.template.api_a.data.remote.service.ApiARemoteDataSourceImpl
import com.septianbeneran.template.api_a.data.repository.EldenRingRepository
import com.septianbeneran.template.api_a.data.repository.EldenRingRepositoryImpl
import com.septianbeneran.template.core.util.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideEldenRingApi(
        retrofit: Retrofit
    ): EldenRingApi {
        return retrofit.create(EldenRingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiARemoteDataSource(
        api: EldenRingApi
    ): ApiARemoteDataSource = ApiARemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideRepository(
        remote: ApiARemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): EldenRingRepository = EldenRingRepositoryImpl(remote, dispatcherProvider)
}