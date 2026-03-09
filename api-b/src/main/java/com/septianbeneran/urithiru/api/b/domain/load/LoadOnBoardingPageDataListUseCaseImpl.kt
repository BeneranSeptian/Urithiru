package com.septianbeneran.urithiru.api.b.domain.load

import com.septianbeneran.urithiru.api.b.repository.ApiJsonBinRepository
import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadOnBoardingPageDataListUseCaseImpl @Inject constructor(
    private val repository: ApiJsonBinRepository
) : LoadOnBoardingPageDataListUseCase {
    override fun invoke() = repository.cache.loadOnBoardingPageDataList()
}