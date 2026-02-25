package com.septianbeneran.template.api.a.di

import com.septianbeneran.template.api.a.data.remote.api.WeaponApi
import com.septianbeneran.template.api.a.data.remote.service.ApiARemoteDataSource
import com.septianbeneran.template.api.a.data.remote.service.ApiARemoteDataSourceImpl
import com.septianbeneran.template.api.a.data.repository.WeaponRepository
import com.septianbeneran.template.api.a.data.repository.WeaponRepositoryImpl
import com.septianbeneran.template.core.util.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiAModule {

    @Provides
    @Singleton
    fun provideEldenRingApi(
        retrofit: Retrofit
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
    fun provideRepository(
        remote: ApiARemoteDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): WeaponRepository = WeaponRepositoryImpl(remote, dispatcherProvider)
}