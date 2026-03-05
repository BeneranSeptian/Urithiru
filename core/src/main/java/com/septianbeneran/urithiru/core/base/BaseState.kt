package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ErrorResponse
import okhttp3.Headers

sealed class BaseState<out T> {

    data object StateInitial: BaseState<Nothing>()
    data object StateLoading: BaseState<Nothing>()
    data class StateSuccess<out T>(val value: T?, val headers: Headers? = null): BaseState<T>()
    data class StateFailed(val failed: ErrorResponse): BaseState<Nothing>()

    fun getData() = if (this is StateSuccess) value else null
    fun getError() = if (this is StateFailed) failed else null
}