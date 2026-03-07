package com.septianbeneran.urithiru.api.a.data.local

import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeaponCacheImpl @Inject constructor(
    private val baseDataStore: BaseDataStore
) : WeaponCache {
    override suspend fun saveWeaponList(weapons: List<Weapon>) {
        baseDataStore.saveObject("WEAPON_LIST", weapons)
    }

    override fun loadWeaponList(): Flow<List<Weapon>> =
        baseDataStore.readObject<List<Weapon>>("WEAPON_LIST").map {
            it ?: emptyList()
        }
}