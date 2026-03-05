package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.ErrorResponse
import retrofit2.Response

abstract class BaseDataSource {
    suspend fun <T> getResult(
        call: suspend () -> Response<T>
    ): ApiResult<T> = try {
        val response = call()
        if (response.isSuccessful) ApiResult.Success(response.body(), response.headers())
        else errorHandler(response.code(), response.message())
    } catch (e: Exception) {
        errorHandler(6969, e.message ?: e.toString())
    }

    private fun <T> errorHandler(
        httpCode: Int,
        errorMessage: String
    ): ApiResult.Error<T> {
        return ApiResult.Error(
            error = ErrorResponse(
                code = httpCode,
                message = errorMessage
            )
        )
    }
}