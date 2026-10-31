package com.septianbeneran.urithiru.api.eldenring.data.remote.service

import com.septianbeneran.urithiru.api.eldenring.BuildConfig.ARMORS_V1
import com.septianbeneran.urithiru.api.eldenring.BuildConfig.BOSSES_V1
import com.septianbeneran.urithiru.api.eldenring.BuildConfig.ITEMS_V1
import com.septianbeneran.urithiru.api.eldenring.BuildConfig.LOCATIONS_V1
import com.septianbeneran.urithiru.api.eldenring.BuildConfig.NPCS_V1
import com.septianbeneran.urithiru.api.eldenring.BuildConfig.WEAPONS_V1
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
        CompendiumKind.Bosses -> getResult { api.getBossList(BOSSES_V1, limit, name, page) }
        CompendiumKind.Weapons -> getResult { api.getWeaponList(WEAPONS_V1, limit, name, page) }
        CompendiumKind.Armors -> getResult { api.getArmorList(ARMORS_V1, limit, name, page) }
        CompendiumKind.Items -> getResult { api.getItemList(ITEMS_V1, limit, name, page) }
        CompendiumKind.Locations -> getResult { api.getLocationList(LOCATIONS_V1, limit, name, page) }
        CompendiumKind.Npcs -> getResult { api.getNpcList(NPCS_V1, limit, name, page) }
    }

    override suspend fun getDetail(
        kind: CompendiumKind,
        id: String
    ): ApiResult<ApiDto<CompendiumResponse>> = when (kind) {
        CompendiumKind.Bosses -> getResult { api.getBossDetail("$BOSSES_V1$id") }
        CompendiumKind.Weapons -> getResult { api.getWeaponDetail("$WEAPONS_V1$id") }
        CompendiumKind.Armors -> getResult { api.getArmorDetail("$ARMORS_V1$id") }
        CompendiumKind.Items -> getResult { api.getItemDetail("$ITEMS_V1$id") }
        CompendiumKind.Locations -> getResult { api.getLocationDetail("$LOCATIONS_V1$id") }
        CompendiumKind.Npcs -> getResult { api.getNpcDetail("$NPCS_V1$id") }
    }
}
