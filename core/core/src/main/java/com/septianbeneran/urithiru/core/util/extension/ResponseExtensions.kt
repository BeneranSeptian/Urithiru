package com.septianbeneran.urithiru.core.util.extension

import com.septianbeneran.urithiru.core.remote.entity.AppError
import retrofit2.Response
import java.io.IOException

private const val MAX_ERROR_MESSAGE_LENGTH = 500

/** Maps an unsuccessful response to an [AppError]. Only `BaseDataSource` should need this. */
internal fun Response<*>.toAppError(): AppError = when (val httpCode = code()) {
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
