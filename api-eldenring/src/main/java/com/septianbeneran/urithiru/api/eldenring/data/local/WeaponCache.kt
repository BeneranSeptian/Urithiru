package com.septianbeneran.urithiru.api.eldenring.data.local

import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import kotlinx.coroutines.flow.Flow

interface WeaponCache {
    suspend fun saveWeaponList(weapons: List<Weapon>)
    fun loadWeaponList(): Flow<List<Weapon>>
}