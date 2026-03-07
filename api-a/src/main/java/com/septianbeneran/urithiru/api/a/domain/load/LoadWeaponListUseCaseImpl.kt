package com.septianbeneran.urithiru.api.a.domain.load

import com.septianbeneran.urithiru.api.a.data.repository.WeaponRepository
import javax.inject.Inject

class LoadWeaponListUseCaseImpl @Inject constructor(
    private val repository: WeaponRepository
): LoadWeaponListUseCase {
    override fun invoke() = repository.cache.loadWeaponList()
}