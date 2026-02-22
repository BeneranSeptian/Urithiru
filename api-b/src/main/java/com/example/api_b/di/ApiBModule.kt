package com.example.api_b.di

import com.example.api_b.data.api.BossApi
import com.example.api_b.data.service.ApiBRemoteDataSource
import com.example.api_b.data.service.ApiBRemoteDataSourceImpl
import com.example.api_b.repository.BossRepository
import com.example.api_b.repository.BossRepositoryImpl
import com.septianbeneran.template.core.util.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ApiBModule {

    @Provides
    @Singleton
    fun provideEldenRingApi(
        retrofit: Retrofit
    ): BossApi {
        return retrofit.create(BossApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiARemoteDataSource(
        api: BossApi
    ): ApiBRemoteDataSource = ApiBRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideRepository(
        remote: ApiBRemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): BossRepository = BossRepositoryImpl(remote, dispatcherProvider)
}