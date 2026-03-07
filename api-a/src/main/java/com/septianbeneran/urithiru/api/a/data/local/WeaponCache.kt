package com.septianbeneran.urithiru.api.a.data.local

import com.septianbeneran.urithiru.core.entity.a.Weapon
import kotlinx.coroutines.flow.Flow

interface WeaponCache {
    suspend fun saveWeaponList(weapons: List<Weapon>)
    fun loadWeaponList(): Flow<List<Weapon>>
}