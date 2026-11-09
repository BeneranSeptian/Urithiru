package com.septianbeneran.urithiru.api.eldenring.data.remote.service

import com.septianbeneran.urithiru.api.eldenring.data.remote.api.CompendiumApi
import com.septianbeneran.urithiru.api.eldenring.data.remote.dto.CompendiumResponse
import com.septianbeneran.urithiru.core.base.BaseDataSource
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.remote.entity.ApiDto
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import javax.inject.Inject

class CompendiumRemoteDataSourceImpl @Inject constructor(
    private val api: CompendiumApi
) : CompendiumRemoteDataSource, BaseDataSource() {

    override suspend fun getList(
        kind: CompendiumKind,
        limit: Int?,
        name: String?,
        page: Int?
    ): ApiResult<ApiDto<List<CompendiumResponse>>> = when (kind) {
        CompendiumKind.Bosses -> getResult { api.getBossList("bosses", limit, name, page) }
        CompendiumKind.Weapons -> getResult { api.getWeaponList("weapons", limit, name, page) }
        CompendiumKind.Armors -> getResult { api.getArmorList("armors", limit, name, page) }
        CompendiumKind.Items -> getResult { api.getItemList("items", limit, name, page) }
        CompendiumKind.Locations -> getResult { api.getLocationList("locations", limit, name, page) }
        CompendiumKind.Npcs -> getResult { api.getNpcList("npcs", limit, name, page) }
    }

    override suspend fun getDetail(
        kind: CompendiumKind,
        id: String
    ): ApiResult<ApiDto<CompendiumResponse>> = when (kind) {
        CompendiumKind.Bosses -> getResult { api.getBossDetail("bosses/$id") }
        CompendiumKind.Weapons -> getResult { api.getWeaponDetail("weapons/$id") }
        CompendiumKind.Armors -> getResult { api.getArmorDetail("armors/$id") }
        CompendiumKind.Items -> getResult { api.getItemDetail("items/$id") }
        CompendiumKind.Locations -> getResult { api.getLocationDetail("locations/$id") }
        CompendiumKind.Npcs -> getResult { api.getNpcDetail("npcs/$id") }
    }
}
