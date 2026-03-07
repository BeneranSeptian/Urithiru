package com.septianbeneran.urithiru.api.a.domain.load

import com.septianbeneran.urithiru.core.entity.a.Weapon
import kotlinx.coroutines.flow.Flow

interface LoadWeaponListUseCase {
    operator fun invoke(): Flow<List<Weapon>>
}