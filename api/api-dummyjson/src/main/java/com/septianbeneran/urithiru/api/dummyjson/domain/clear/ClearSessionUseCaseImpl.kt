package com.septianbeneran.urithiru.api.dummyjson.domain.clear

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class ClearSessionUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : ClearSessionUseCase {
    override suspend fun invoke() = repository.logout()
}
