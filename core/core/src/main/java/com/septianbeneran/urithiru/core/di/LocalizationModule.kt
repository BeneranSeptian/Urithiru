package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.localization.AssetTranslator
import com.septianbeneran.urithiru.core.localization.Translator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalizationModule {

    @Binds
    @Singleton
    abstract fun bindTranslator(impl: AssetTranslator): Translator
}
