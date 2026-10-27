package com.septianbeneran.urithiru.api.dummyjson.di

import com.septianbeneran.urithiru.api.dummyjson.domain.load.LoadSessionUseCase
import com.septianbeneran.urithiru.api.dummyjson.domain.load.LoadSessionUseCaseImpl
import com.septianbeneran.urithiru.api.dummyjson.domain.post.PostLoginUseCase
import com.septianbeneran.urithiru.api.dummyjson.domain.post.PostLoginUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindPostLoginUseCase(impl: PostLoginUseCaseImpl): PostLoginUseCase

    @Binds
    abstract fun bindLoadSessionUseCase(impl: LoadSessionUseCaseImpl): LoadSessionUseCase
}
