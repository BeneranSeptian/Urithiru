package com.septianbeneran.urithiru.api.dummyjson.domain.load

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class LoadSessionUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : LoadSessionUseCase {
    override fun invoke() = repository.observeSession()
}
