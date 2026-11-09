package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

open class BaseRepository {
    /**
     * Maps a successful response to an entity, then runs [saveResult] before emitting `Success`,
     * so `Success` means the result is already cached. The save runs in [NonCancellable]: it still
     * finishes when the collector (e.g. a closed screen's ViewModel) is cancelled mid-write.
     * A mapping failure becomes [AppError.Parse]; a failed save becomes [AppError.Unknown].
     */
    private suspend fun <ResponsePlain, Entity> ApiResult<ResponsePlain>.transformResult(
        transform: (ResponsePlain?) -> Entity,
        saveResult: (suspend (Entity) -> Unit)? = null
    ): ApiResult<Entity & Any> = when (this) {
        is ApiResult.Error -> ApiResult.Error(error)
        is ApiResult.Loading -> ApiResult.Loading()
        is ApiResult.Success -> {
            val entity = try {
                transform(data)
            } catch (e: Exception) {
                return ApiResult.Error(AppError.Parse(e))
            }
            try {
                saveResult?.let { save -> withContext(NonCancellable) { save(entity) } }
                ApiResult.Success(entity)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ApiResult.Error(AppError.Unknown(e))
            }
        }
    }

    protected fun <ResponsePlain, Entity> Flow<ApiResult<ResponsePlain>>.mapToEntity(
        transform: (ResponsePlain?) -> Entity,
        saveResult: (suspend (Entity) -> Unit)? = null
    ): Flow<ApiResult<Entity & Any>> = map { it.transformResult(transform, saveResult) }
}
