package com.example.feature_b.di

import com.example.core_navigation.FeatureNavGraph
import com.example.feature_b.navigation.FeatureBNavGraphImpl
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