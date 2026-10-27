package com.septianbeneran.urithiru.api.eldenring.di

import com.septianbeneran.urithiru.api.eldenring.domain.get.GetWeaponDetailUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetWeaponDetailUseCaseImpl
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetWeaponListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.get.GetWeaponListUseCaseImpl
import com.septianbeneran.urithiru.api.eldenring.domain.load.LoadWeaponListUseCase
import com.septianbeneran.urithiru.api.eldenring.domain.load.LoadWeaponListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetWeaponListUseCase(impl: GetWeaponListUseCaseImpl): GetWeaponListUseCase

    @Binds
    abstract fun bindGetWeaponDetailUseCase(impl: GetWeaponDetailUseCaseImpl): GetWeaponDetailUseCase

    @Binds
    abstract fun bindLoadWeaponListUseCase(impl: LoadWeaponListUseCaseImpl): LoadWeaponListUseCase
}
