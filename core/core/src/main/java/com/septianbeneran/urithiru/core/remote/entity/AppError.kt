package com.septianbeneran.urithiru.core.remote.entity

/**
 * Every way a request can fail. [BaseDataSource.getResult] and [BaseRepository.mapToEntity]
 * map failures to one of these, so the UI can pick a message and decide whether to offer a retry.
 */
sealed interface AppError {
    /** No connection, or the host can't be reached. */
    data object NoInternet : AppError

    data object Timeout : AppError

    /** HTTP 401. */
    data object Unauthorized : AppError

    /** HTTP 404. */
    data object NotFound : AppError

    /** HTTP 5xx. */
    data class Server(val code: Int) : AppError

    /** Any other non-2xx response. [message] is the server's error body when it sent one. */
    data class Http(val code: Int, val message: String?) : AppError

    /** The response couldn't be decoded or mapped to an entity. */
    data class Parse(val cause: Throwable) : AppError

    data class Unknown(val cause: Throwable) : AppError

    val isRetryable: Boolean
        get() = when (this) {
            NoInternet, Timeout, is Server, is Unknown -> true
            Unauthorized, NotFound, is Http, is Parse -> false
        }
}
