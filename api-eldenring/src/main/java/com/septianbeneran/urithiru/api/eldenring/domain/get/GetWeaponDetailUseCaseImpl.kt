package com.septianbeneran.urithiru.api.eldenring.domain.get

import com.septianbeneran.urithiru.api.eldenring.repository.WeaponRepository
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeaponDetailUseCaseImpl @Inject constructor(
    private val repo: WeaponRepository
) : GetWeaponDetailUseCase {
    override fun invoke(weaponId: String): Flow<ApiResult<Weapon>> = repo.getWeaponDetail(weaponId)
}