package com.septianbeneran.template.api.a.domain.get

import com.septianbeneran.template.api.a.data.repository.WeaponRepository
import com.septianbeneran.template.core.entity.a.Weapon
import com.septianbeneran.template.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeaponDetailUseCaseImpl @Inject constructor(
    private val repo: WeaponRepository
) : GetWeaponDetailUseCase {
    override fun invoke(weaponId: String): Flow<ApiResult<Weapon>> = repo.getWeaponDetail(weaponId)
}