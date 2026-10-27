package com.septianbeneran.urithiru.api.eldenring.domain.load

import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import kotlinx.coroutines.flow.Flow

interface LoadWeaponListUseCase {
    operator fun invoke(): Flow<List<Weapon>>
}