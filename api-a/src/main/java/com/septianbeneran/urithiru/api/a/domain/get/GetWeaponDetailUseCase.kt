package com.septianbeneran.urithiru.api.a.domain.get

import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetWeaponDetailUseCase {
    operator fun invoke(weaponId: String): Flow<ApiResult<Weapon>>
}