package com.septianbeneran.urithiru.api.b.domain.load

import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import kotlinx.coroutines.flow.Flow

interface LoadOnBoardingPageDataListUseCase {
    operator fun invoke(): Flow<List<OnBoardingPageData>>
}