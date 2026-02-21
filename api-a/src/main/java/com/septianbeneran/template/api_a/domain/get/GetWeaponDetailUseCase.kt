package com.septianbeneran.template.api_a.domain.get

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core_entity.weapon.Weapon
import kotlinx.coroutines.flow.Flow

interface GetWeaponDetailUseCase {
    operator fun invoke(weaponId: String): Flow<ApiResult<Weapon>>
}