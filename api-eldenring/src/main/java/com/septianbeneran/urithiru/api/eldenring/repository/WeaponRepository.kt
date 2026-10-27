package com.septianbeneran.urithiru.api.eldenring.repository

import com.septianbeneran.urithiru.api.eldenring.data.local.WeaponCache
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
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