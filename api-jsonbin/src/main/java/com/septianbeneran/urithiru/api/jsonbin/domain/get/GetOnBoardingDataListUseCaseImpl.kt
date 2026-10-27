package com.septianbeneran.urithiru.api.jsonbin.domain.get

import com.septianbeneran.urithiru.api.jsonbin.repository.ApiJsonBinRepository
import javax.inject.Inject

class GetOnBoardingDataListUseCaseImpl @Inject constructor(
    private val repository: ApiJsonBinRepository
) : GetOnBoardingDataListUseCase {
    override fun invoke(binId: String) = repository.getOnBoardingPageDataList(binId)
}