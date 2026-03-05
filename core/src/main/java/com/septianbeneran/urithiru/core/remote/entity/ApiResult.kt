package com.septianbeneran.urithiru.core.remote.entity

import okhttp3.Headers

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T?, val headers: Headers? = null) : ApiResult<T>()
    data class Error<out T>(val error: ErrorResponse) : ApiResult<T>()
    class Loading<out T> : ApiResult<T>()
}
