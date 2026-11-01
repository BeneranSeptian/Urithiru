package com.septianbeneran.urithiru.di

import com.septianbeneran.urithiru.UrithiruProject
import com.septianbeneran.urithiru.core.annotation.DefaultThemeMode
import com.septianbeneran.urithiru.core.settings.ThemeMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @DefaultThemeMode
    fun provideDefaultThemeMode(): ThemeMode = UrithiruProject.defaultThemeMode
}
