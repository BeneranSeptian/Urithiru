package com.septianbeneran.urithiru.api.eldenring.data.remote.service

import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.WeaponResponse
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult

interface WeaponRemoteDataSource {

    suspend fun getWeaponList(
        limit: Int? = null,
        name: String? = null,
        page: Int? = null
    ): ApiResult<ApiDto<List<WeaponResponse>>>

    suspend fun getWeaponDetail(
        weaponId: String
    ): ApiResult<ApiDto<WeaponResponse>>
}