package com.septianbeneran.urithiru.api.dummyjson.domain.load

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import kotlinx.coroutines.flow.Flow

interface LoadSessionUseCase {
    operator fun invoke(): Flow<AuthSession?>
}
