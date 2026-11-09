package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.DefaultDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider =
        DefaultDispatcherProvider()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        dispatcherProvider: CoroutineDispatcherProvider
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.default())
}