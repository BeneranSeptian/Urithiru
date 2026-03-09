package com.septianbeneran.urithiru.api.b.domain.get

import com.septianbeneran.urithiru.api.b.repository.ApiJsonBinRepository
import javax.inject.Inject

class GetOnBoardingDataListUseCaseImpl @Inject constructor(
    private val repository: ApiJsonBinRepository
) : GetOnBoardingDataListUseCase {
    override fun invoke(binId: String) = repository.getOnBoardingPageDataList(binId)
}