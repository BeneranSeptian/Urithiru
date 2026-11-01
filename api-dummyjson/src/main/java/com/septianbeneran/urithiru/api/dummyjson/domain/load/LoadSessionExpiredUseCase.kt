package com.septianbeneran.urithiru.api.dummyjson.domain.load

import kotlinx.coroutines.flow.Flow

interface LoadSessionExpiredUseCase {
    operator fun invoke(): Flow<Boolean>
}
