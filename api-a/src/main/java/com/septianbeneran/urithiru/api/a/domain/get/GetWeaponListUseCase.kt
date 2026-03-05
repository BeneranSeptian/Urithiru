package com.septianbeneran.urithiru.api.a.domain.get

import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetWeaponListUseCase {
    operator fun invoke(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Weapon>>>
}