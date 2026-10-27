package com.septianbeneran.urithiru.api.dummyjson.domain.post

import com.septianbeneran.urithiru.api.dummyjson.repository.AuthRepository
import javax.inject.Inject

class PostLoginUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : PostLoginUseCase {
    override fun invoke(username: String, password: String) = repository.login(username, password)
}
