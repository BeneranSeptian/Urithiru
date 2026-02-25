package com.example.feature.b.di

import com.example.core.navigation.util.FeatureNavGraph
import com.example.feature.b.navigation.FeatureBNavGraphImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface FeatureBModule {
    @Binds
    @IntoSet
    fun bindFeatureBNavgraph(impl: FeatureBNavGraphImpl): FeatureNavGraph
}