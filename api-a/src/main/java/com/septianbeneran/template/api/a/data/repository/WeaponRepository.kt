package com.septianbeneran.template.api.a.data.repository

import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.entity.a.Weapon
import kotlinx.coroutines.flow.Flow

interface WeaponRepository {
    fun getWeaponList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Weapon>>>

    fun getWeaponDetail(
        weaponId: String
    ): Flow<ApiResult<Weapon>>
}