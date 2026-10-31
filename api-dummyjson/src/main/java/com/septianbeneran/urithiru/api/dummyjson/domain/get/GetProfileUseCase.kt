package com.septianbeneran.urithiru.api.dummyjson.domain.get

import com.septianbeneran.urithiru.core.entity.auth.UserProfile
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetProfileUseCase {
    operator fun invoke(): Flow<ApiResult<UserProfile>>
}
