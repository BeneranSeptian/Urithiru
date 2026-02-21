package com.septianbeneran.template.api_a.domain.get

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core_entity.weapon.Weapon
import kotlinx.coroutines.flow.Flow

interface GetWeaponListUseCase {
    operator fun invoke(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Weapon>>>
}