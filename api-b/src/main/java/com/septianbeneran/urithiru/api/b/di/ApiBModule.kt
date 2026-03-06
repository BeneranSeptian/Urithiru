package com.septianbeneran.urithiru.api.b.di

import com.septianbeneran.urithiru.api.b.data.api.BossApi
import com.septianbeneran.urithiru.api.b.data.service.ApiBRemoteDataSource
import com.septianbeneran.urithiru.api.b.data.service.ApiBRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.b.repository.BossRepository
import com.septianbeneran.urithiru.api.b.repository.BossRepositoryImpl
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ApiBModule {

    @Provides
    @Singleton
    fun provideEldenRingApi(
        @EldenRingNetwork retrofit: Retrofit
    ): BossApi {
        return retrofit.create(BossApi::class.java)
    }

    @Provides
    @Singleton
    fun provideApiBRemoteDataSource(
        api: BossApi
    ): ApiBRemoteDataSource = ApiBRemoteDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideRepository(
        remote: ApiBRemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): BossRepository = BossRepositoryImpl(remote, dispatcherProvider)
}