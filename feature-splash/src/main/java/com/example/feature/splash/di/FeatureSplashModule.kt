package com.example.feature.splash.di

import com.example.core.navigation.util.FeatureNavGraph
import com.example.feature.splash.navigation.FeatureSplashNavGraphImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface FeatureSplashModule {
    @Binds
    @IntoSet
    fun bindFeatureSplashNavgraph(impl: FeatureSplashNavGraphImpl): FeatureNavGraph
}