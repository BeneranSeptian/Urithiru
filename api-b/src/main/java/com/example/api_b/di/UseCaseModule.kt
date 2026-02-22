package com.example.api_b.di

import com.example.api_b.domain.get.GetBossListUseCase
import com.example.api_b.domain.get.GetBossListUseCaseImpl
import com.example.api_b.repository.BossRepository
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
    internal fun provideGetWeaponListUseCase(
        repo: BossRepository
    ): GetBossListUseCase = GetBossListUseCaseImpl(repo)
}