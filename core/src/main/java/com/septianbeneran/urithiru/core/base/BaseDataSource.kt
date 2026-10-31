package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

    private fun Response<*>.toAppError(): AppError = when (val httpCode = code()) {
        401 -> AppError.Unauthorized
        404 -> AppError.NotFound
        in 500..599 -> AppError.Server(httpCode)
        else -> AppError.Http(httpCode, errorMessage())
    }

    private fun Response<*>.errorMessage(): String? {
        val body = try {
            errorBody()?.string()
        } catch (_: IOException) {
            null
        }
        return body?.takeIf { it.isNotBlank() }?.take(MAX_ERROR_MESSAGE_LENGTH)
            ?: message().takeIf { it.isNotBlank() }
    }

    private fun Exception.toAppError(): AppError = when (this) {
        is SocketTimeoutException -> AppError.Timeout
        is UnknownHostException -> AppError.NoInternet
        is SerializationException -> AppError.Parse(this)
        is IOException -> AppError.NoInternet
        else -> AppError.Unknown(this)
    }

    private companion object {
        const val MAX_ERROR_MESSAGE_LENGTH = 500
    }
}
