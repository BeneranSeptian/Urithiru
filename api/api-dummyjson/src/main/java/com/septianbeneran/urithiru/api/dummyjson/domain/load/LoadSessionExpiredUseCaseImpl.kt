package com.septianbeneran.urithiru.api.dummyjson.domain.load

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class LoadSessionExpiredUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : LoadSessionExpiredUseCase {
    override fun invoke() = repository.observeSessionExpired()
}
