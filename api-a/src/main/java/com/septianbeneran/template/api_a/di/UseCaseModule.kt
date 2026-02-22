package com.septianbeneran.template.api_a.di

import com.septianbeneran.template.api_a.data.repository.WeaponRepository
import com.septianbeneran.template.api_a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.template.api_a.domain.get.GetWeaponDetailUseCaseImpl
import com.septianbeneran.template.api_a.domain.get.GetWeaponListUseCase
import com.septianbeneran.template.api_a.domain.get.GetWeaponListUseCaseImpl
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
        repo: WeaponRepository
    ): GetWeaponListUseCase = GetWeaponListUseCaseImpl(repo)

    @Singleton
    @Provides
    internal fun provideGetWeaponDetail(
        repo: WeaponRepository
    ): GetWeaponDetailUseCase = GetWeaponDetailUseCaseImpl(repo)
}