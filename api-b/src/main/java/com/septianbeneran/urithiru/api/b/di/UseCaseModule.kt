package com.septianbeneran.urithiru.api.b.di

import com.septianbeneran.urithiru.api.b.domain.get.GetOnBoardingDataListUseCase
import com.septianbeneran.urithiru.api.b.domain.get.GetOnBoardingDataListUseCaseImpl
import com.septianbeneran.urithiru.api.b.domain.load.LoadOnBoardingPageDataListUseCase
import com.septianbeneran.urithiru.api.b.domain.load.LoadOnBoardingPageDataListUseCaseImpl
import com.septianbeneran.urithiru.api.b.repository.ApiJsonBinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Singleton
    @Provides
    internal fun provideGetOnBoardingDataListUseCase(
        repo: ApiJsonBinRepository
    ): GetOnBoardingDataListUseCase = GetOnBoardingDataListUseCaseImpl(repo)

    @Singleton
    @Provides
    internal fun provideLoadOnBoardingPageDataListUseCase(
        repo: ApiJsonBinRepository
    ): LoadOnBoardingPageDataListUseCase = LoadOnBoardingPageDataListUseCaseImpl(repo)
}