package com.example.feature.a.di

import com.example.core.navigation.util.FeatureNavGraph
import com.example.feature.a.navigation.FeatureANavGraphImpl
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
