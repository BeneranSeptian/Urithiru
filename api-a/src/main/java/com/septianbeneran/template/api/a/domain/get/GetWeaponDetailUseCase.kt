package com.septianbeneran.template.api.a.domain.get

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.entity.a.Weapon
import kotlinx.coroutines.flow.Flow

interface GetWeaponDetailUseCase {
    operator fun invoke(weaponId: String): Flow<ApiResult<Weapon>>
}