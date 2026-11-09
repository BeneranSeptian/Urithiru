package com.septianbeneran.urithiru.api.jsonbin.domain.get

import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow

interface GetOnBoardingDataListUseCase {
    operator fun invoke(
        binId: String
    ): Flow<ApiResult<List<OnBoardingPageData>>>
}