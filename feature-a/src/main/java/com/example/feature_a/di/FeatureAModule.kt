package com.example.feature_a.di

import com.example.core_navigation.FeatureNavGraph
import com.example.feature_a.navigation.FeatureANavGraphImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface FeatureAModule {
    @Binds
    @IntoSet
    fun bindFeatureANavgraph(impl: FeatureANavGraphImpl): FeatureNavGraph
}
