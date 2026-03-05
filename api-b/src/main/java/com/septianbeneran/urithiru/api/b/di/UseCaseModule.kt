package com.septianbeneran.urithiru.api.b.di

import com.septianbeneran.urithiru.api.b.domain.get.GetBossListUseCase
import com.septianbeneran.urithiru.api.b.domain.get.GetBossListUseCaseImpl
import com.septianbeneran.urithiru.api.b.repository.BossRepository
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