package com.septianbeneran.urithiru.api.jsonbin.domain.load

import com.septianbeneran.urithiru.core.entity.onboarding.OnBoardingPageData
import kotlinx.coroutines.flow.Flow

interface LoadOnBoardingPageDataListUseCase {
    operator fun invoke(): Flow<List<OnBoardingPageData>>
}