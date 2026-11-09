package com.septianbeneran.urithiru.core.util.extension

import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Maps a failed call's exception to an [AppError]. Only `BaseDataSource` should need this. */
internal fun Exception.toAppError(): AppError = when (this) {
    is SocketTimeoutException -> AppError.Timeout
    is UnknownHostException -> AppError.NoInternet
    is SerializationException -> AppError.Parse(this)
    is IOException -> AppError.NoInternet
    else -> AppError.Unknown(this)
}
