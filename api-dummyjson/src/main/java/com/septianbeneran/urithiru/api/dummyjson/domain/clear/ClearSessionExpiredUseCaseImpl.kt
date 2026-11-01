package com.septianbeneran.urithiru.api.dummyjson.domain.clear

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class ClearSessionExpiredUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : ClearSessionExpiredUseCase {
    override suspend fun invoke() = repository.dismissSessionExpired()
}
