package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.extension.toAppError
import kotlinx.coroutines.CancellationException
import retrofit2.Response

abstract class BaseDataSource {
    suspend fun <T> getResult(
        call: suspend () -> Response<T>
    ): ApiResult<T> = try {
        val response = call()
        if (response.isSuccessful) ApiResult.Success(response.body(), response.headers())
        else ApiResult.Error(response.toAppError())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(e.toAppError())
    }
}
