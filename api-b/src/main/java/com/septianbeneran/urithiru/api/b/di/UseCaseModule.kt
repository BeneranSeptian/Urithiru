package com.septianbeneran.urithiru.api.b.di

import com.septianbeneran.urithiru.api.b.domain.get.GetOnBoardingDataListUseCase
import com.septianbeneran.urithiru.api.b.domain.get.GetOnBoardingDataListUseCaseImpl
import com.septianbeneran.urithiru.api.b.domain.load.LoadOnBoardingPageDataListUseCase
import com.septianbeneran.urithiru.api.b.domain.load.LoadOnBoardingPageDataListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetOnBoardingDataListUseCase(impl: GetOnBoardingDataListUseCaseImpl): GetOnBoardingDataListUseCase

    @Binds
    abstract fun bindLoadOnBoardingPageDataListUseCase(impl: LoadOnBoardingPageDataListUseCaseImpl): LoadOnBoardingPageDataListUseCase
}