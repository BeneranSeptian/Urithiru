package com.septianbeneran.urithiru.api.eldenring.repository

import com.septianbeneran.urithiru.api.eldenring.data.local.WeaponCache
import com.septianbeneran.urithiru.api.eldenring.data.remote.service.WeaponRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeaponRepositoryImpl @Inject constructor(
    private val remote: WeaponRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider,
    @ApplicationScope private val applicationScope: CoroutineScope,
    override val cache: WeaponCache
) : BaseRepository(applicationScope),WeaponRepository {
    override fun getWeaponList(limit: Int?, name: String?, page: Int?) =
        resultFlow(
            networkCall = { remote.getWeaponList(limit, name, page) },
            dispatcher = dispatcher
        ).mapToEntity(
            transform = { it?.data?.map { weaponResponse -> weaponResponse.mapToWeapon() } },
            saveResult = {
                it?.let { cache.saveWeaponList(it) }
            }
        )

    override fun getWeaponDetail(weaponId: String): Flow<ApiResult<Weapon>> =
        resultFlow(
            networkCall = {remote.getWeaponDetail(weaponId)},
            dispatcher = dispatcher
        ).mapToEntity(
            transform = { it?.data?.mapToWeapon() }
        )
}