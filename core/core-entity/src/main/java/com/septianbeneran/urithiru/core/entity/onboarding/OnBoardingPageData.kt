package com.septianbeneran.urithiru.core.entity.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class OnBoardingPageData(
    val imgUrl: String? = null,
    val title: String,
    val description: String
)