package com.septianbeneran.urithiru.core.base

import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.AppError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseRepositoryTest {

    private class TestRepository : BaseRepository() {
        fun <R, E> map(
            source: Flow<ApiResult<R>>,
            transform: (R?) -> E,
            saveResult: (suspend (E) -> Unit)? = null
        ) = source.mapToEntity(transform, saveResult)
    }

    private val repository = TestRepository()

    @Test
    fun successIsEmittedOnlyAfterTheSaveFinished() = runBlocking {
        val saved = mutableListOf<Int>()

        val result = repository.map(
            source = flowOf(ApiResult.Success("42")),
            transform = { it!!.toInt() },
            saveResult = { saved += it }
        ).first()

        assertTrue(result is ApiResult.Success)
        assertEquals(listOf(42), saved)
    }

    @Test
    fun aFailedMappingBecomesAParseError() = runBlocking {
        val result = repository.map(
            source = flowOf(ApiResult.Success("not a number")),
            transform = { it!!.toInt() }
        ).first()

        assertTrue((result as ApiResult.Error).error is AppError.Parse)
    }

    @Test
    fun aFailedSaveBecomesAnUnknownError() = runBlocking {
        val result = repository.map(
            source = flowOf(ApiResult.Success("42")),
            transform = { it!!.toInt() },
            saveResult = { error("disk full") }
        ).first()

        assertTrue((result as ApiResult.Error).error is AppError.Unknown)
    }

    @Test
    fun theSaveStillFinishesWhenTheCollectorIsCancelled() = runBlocking {
        val saveStarted = CompletableDeferred<Unit>()
        val releaseSave = CompletableDeferred<Unit>()
        val saveFinished = CompletableDeferred<Int>()

        val collector = launch(Dispatchers.Default) {
            repository.map(
                source = flowOf(ApiResult.Success("42")),
                transform = { it!!.toInt() },
                saveResult = {
                    saveStarted.complete(Unit)
                    releaseSave.await()
                    saveFinished.complete(it)
                }
            ).toList()
        }

        saveStarted.await()
        collector.cancel()
        yield()
        releaseSave.complete(Unit)

        assertEquals(42, withTimeout(1_000) { saveFinished.await() })
    }

    @Test
    fun loadingAndErrorsPassThroughUnchanged() = runBlocking {
        val results = repository.map(
            source = flowOf(ApiResult.Loading(), ApiResult.Error(AppError.NoInternet)),
            transform = { it: String? -> it }
        ).toList()

        assertTrue(results[0] is ApiResult.Loading)
        assertEquals(AppError.NoInternet, (results[1] as ApiResult.Error).error)
    }
}
