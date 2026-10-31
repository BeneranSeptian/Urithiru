package com.septianbeneran.urithiru.api.dummyjson.domain.get

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class GetProfileUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : GetProfileUseCase {
    override fun invoke() = repository.getProfile()
}
