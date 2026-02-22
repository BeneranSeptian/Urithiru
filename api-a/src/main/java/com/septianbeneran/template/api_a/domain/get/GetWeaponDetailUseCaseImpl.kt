package com.septianbeneran.template.api_a.domain.get

import com.septianbeneran.template.api_a.data.repository.WeaponRepository
import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core_entity.a.Weapon
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeaponDetailUseCaseImpl @Inject constructor(
    private val repo: WeaponRepository
) : GetWeaponDetailUseCase {
    override fun invoke(weaponId: String): Flow<ApiResult<Weapon>> = repo.getWeaponDetail(weaponId)
}