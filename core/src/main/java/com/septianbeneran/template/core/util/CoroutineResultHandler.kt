package com.septianbeneran.template.core.util

import com.septianbeneran.template.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <A> resultFlow(
    networkCall: suspend () -> ApiResult<A>,
    dispatcher: CoroutineDispatcherProvider
): Flow<ApiResult<A>> = flow {
    emit(ApiResult.Loading())
    when (val response = networkCall()) {
        is ApiResult.Success -> emit(ApiResult.Success(response.data, response.headers))
        is ApiResult.Loading -> emit(ApiResult.Loading())
        is ApiResult.Error -> emit(ApiResult.Error(response.error))
    }
}.flowOn(dispatcher.io())

