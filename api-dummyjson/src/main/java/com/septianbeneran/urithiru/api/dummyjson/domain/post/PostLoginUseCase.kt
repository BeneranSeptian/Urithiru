package com.septianbeneran.urithiru.api.dummyjson.domain.post

import com.septianbeneran.urithiru.core.entity.auth.AuthSession
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface PostLoginUseCase {
    operator fun invoke(username: String, password: String): Flow<ApiResult<AuthSession>>
}
