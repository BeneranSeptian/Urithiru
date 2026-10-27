package com.septianbeneran.urithiru.api.eldenring.domain.load

import com.septianbeneran.urithiru.api.eldenring.repository.WeaponRepository
import javax.inject.Inject

class LoadWeaponListUseCaseImpl @Inject constructor(
    private val repository: WeaponRepository
): LoadWeaponListUseCase {
    override fun invoke() = repository.cache.loadWeaponList()
}