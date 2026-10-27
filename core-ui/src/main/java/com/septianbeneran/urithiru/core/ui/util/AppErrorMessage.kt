package com.septianbeneran.urithiru.core.ui.util

import androidx.annotation.StringRes
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.R

/** A user-facing message for this error, as a string resource so it can be translated. */
@StringRes
fun AppError.toMessage(): Int = when (this) {
    AppError.NoInternet -> R.string.error_no_internet
    AppError.Timeout -> R.string.error_timeout
    AppError.Unauthorized -> R.string.error_unauthorized
    AppError.NotFound -> R.string.error_not_found
    is AppError.Server -> R.string.error_server
    is AppError.Http -> R.string.error_http
    is AppError.Parse -> R.string.error_parse
    is AppError.Unknown -> R.string.error_unknown
}
