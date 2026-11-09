package com.septianbeneran.urithiru.api.eldenring.di

import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumDetailUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumDetailUseCaseImpl
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumFeedPageUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumFeedPageUseCaseImpl
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetCompendiumListUseCaseImpl
import com.septianbeneran.urithiru.api.eldenring.domain.get.SearchCompendiumUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.SearchCompendiumUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetCompendiumListUseCase(impl: GetCompendiumListUseCaseImpl): GetCompendiumListUseCase

    @Binds
    abstract fun bindGetCompendiumDetailUseCase(impl: GetCompendiumDetailUseCaseImpl): GetCompendiumDetailUseCase

    @Binds
    abstract fun bindGetCompendiumFeedPageUseCase(impl: GetCompendiumFeedPageUseCaseImpl): GetCompendiumFeedPageUseCase

    @Binds
    abstract fun bindSearchCompendiumUseCase(impl: SearchCompendiumUseCaseImpl): SearchCompendiumUseCase
}
