package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class BaseRepository {
    private fun <ResponsePlain, Entity> ApiResult<ResponsePlain>.transformResult(
        transform: (ResponsePlain?) -> Entity,
        saveResult: ((Entity)->Unit)? = null
    ) = try {
        when(this) {
            is ApiResult.Error -> ApiResult.Error(error)
            is ApiResult.Loading -> ApiResult.Loading()
            is ApiResult.Success -> ApiResult.Success(transform.invoke(data))
        }
    } catch (t: Throwable) {
        ApiResult.Error(ErrorResponse(6969, t.message))
    }

    protected fun <ResponsePlain, Entity> Flow<ApiResult<ResponsePlain>>.mapToEntity(
        transform: (ResponsePlain?) -> Entity,
        saveResult: ((Entity)-> Unit)? = null
    ) = this.map {
        it.transformResult(transform)
    }


}