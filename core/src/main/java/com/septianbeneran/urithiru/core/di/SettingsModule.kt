package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.settings.AppSettings
import com.septianbeneran.urithiru.core.settings.AppSettingsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindAppSettings(impl: AppSettingsImpl): AppSettings
}
