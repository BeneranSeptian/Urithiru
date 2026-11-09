package com.septianbeneran.urithiru.api.eldenring.di

import com.septianbeneran.urithiru.api.eldenring.data.remote.api.CompendiumApi
import com.septianbeneran.urithiru.api.eldenring.data.remote.service.CompendiumRemoteDataSource
import com.septianbeneran.urithiru.api.eldenring.data.remote.service.CompendiumRemoteDataSourceImpl
import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepository
import com.septianbeneran.urithiru.api.eldenring.repository.CompendiumRepositoryImpl
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
    abstract fun bindCompendiumRemoteDataSource(impl: CompendiumRemoteDataSourceImpl): CompendiumRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCompendiumRepository(impl: CompendiumRepositoryImpl): CompendiumRepository

    companion object {
        @Provides
        @Singleton
        fun provideCompendiumApi(
            @EldenRingNetwork retrofit: Retrofit
        ): CompendiumApi = retrofit.create(CompendiumApi::class.java)
    }
}
