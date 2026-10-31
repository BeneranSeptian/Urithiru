package com.septianbeneran.urithiru.api.eldenring.repository

import com.septianbeneran.urithiru.api.eldenring.data.remote.service.CompendiumRemoteDataSource
import com.septianbeneran.urithiru.core.annotation.ApplicationScope
import com.septianbeneran.urithiru.core.base.BaseRepository
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumPage
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.util.CoroutineDispatcherProvider
import com.septianbeneran.urithiru.core.util.resultFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompendiumRepositoryImpl @Inject constructor(
    private val remote: CompendiumRemoteDataSource,
    private val dispatcher: CoroutineDispatcherProvider,
    @ApplicationScope applicationScope: CoroutineScope
) : BaseRepository(applicationScope), CompendiumRepository {

    override fun getList(
        kind: CompendiumKind,
        limit: Int?,
        name: String?,
        page: Int?
    ): Flow<ApiResult<CompendiumPage>> =
        resultFlow(
            networkCall = { remote.getList(kind, limit, name, page) },
            dispatcher = dispatcher
        ).mapToEntity(
            transform = { dto ->
                CompendiumPage(
                    entries = dto?.data.orEmpty().map { it.mapToEntry() },
                    total = dto?.total
                )
            }
        )

    override fun getDetail(kind: CompendiumKind, id: String): Flow<ApiResult<CompendiumEntry>> =
        resultFlow(
            networkCall = { remote.getDetail(kind, id) },
            dispatcher = dispatcher
        ).mapToEntity(
            transform = { it?.data?.mapToEntry() }
        )
}
