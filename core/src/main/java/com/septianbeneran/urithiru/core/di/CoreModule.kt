package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.DefaultDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider =
        DefaultDispatcherProvider()
}