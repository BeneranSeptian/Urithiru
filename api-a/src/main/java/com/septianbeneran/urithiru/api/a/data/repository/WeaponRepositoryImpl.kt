package com.septianbeneran.urithiru.api.a.data.repository

import com.septianbeneran.urithiru.api.a.data.remote.service.ApiARemoteDataSource
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeaponRepositoryImpl @Inject constructor(
    private val remote: ApiARemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider
) : WeaponRepository, BaseRepository() {
    override fun getWeaponList(limit: Int?, name: String?, page: Int?) =
        resultFlow(
            networkCall = { remote.getWeaponList(limit, name, page) },
            dispatcher = dispatcher
        ).mapToEntity(
            transform = { it?.data?.map { weaponResponse -> weaponResponse.mapToWeapon() } }
        )

    override fun getWeaponDetail(weaponId: String): Flow<ApiResult<Weapon>> =
        resultFlow(
            networkCall = {remote.getWeaponDetail(weaponId)},
            dispatcher = dispatcher
        ).mapToEntity { it?.data?.mapToWeapon() }
}