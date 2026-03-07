package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.ErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class BaseRepository (
    private val applicationScope: CoroutineScope
) {
    private fun <ResponsePlain, Entity> ApiResult<ResponsePlain>.transformResult(
        transform: (ResponsePlain?) -> Entity,
        saveResult: (suspend (Entity)->Unit)? = null
    ) = try {
        when(this) {
            is ApiResult.Error -> ApiResult.Error(error)
            is ApiResult.Loading -> ApiResult.Loading()
            is ApiResult.Success -> {
                val entity = transform.invoke(data)
                applicationScope.launch {
                    saveResult?.invoke(entity)
                }
                ApiResult.Success(entity)
            }
        }
    } catch (t: Throwable) {
        ApiResult.Error(ErrorResponse(6969, t.message))
    }

    protected fun <ResponsePlain, Entity> Flow<ApiResult<ResponsePlain>>.mapToEntity(
        transform: (ResponsePlain?) -> Entity,
        saveResult: (suspend (Entity)-> Unit)? = null
    ) = this.map {
        it.transformResult(transform, saveResult)
    }
}