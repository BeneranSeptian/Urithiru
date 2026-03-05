package com.septianbeneran.urithiru.api.a.di

import com.septianbeneran.urithiru.api.a.data.repository.WeaponRepository
import com.septianbeneran.urithiru.api.a.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.urithiru.api.a.domain.get.GetWeaponDetailUseCaseImpl
import com.septianbeneran.urithiru.api.a.domain.get.GetWeaponListUseCase
import com.septianbeneran.urithiru.api.a.domain.get.GetWeaponListUseCaseImpl
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