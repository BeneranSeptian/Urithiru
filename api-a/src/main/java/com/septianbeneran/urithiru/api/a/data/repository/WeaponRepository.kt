package com.septianbeneran.urithiru.api.a.data.repository

import com.septianbeneran.urithiru.api.a.data.local.WeaponCache
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface WeaponRepository {
    val cache: WeaponCache
    fun getWeaponList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): Flow<ApiResult<List<Weapon>>>

    fun getWeaponDetail(
        weaponId: String
    ): Flow<ApiResult<Weapon>>
}