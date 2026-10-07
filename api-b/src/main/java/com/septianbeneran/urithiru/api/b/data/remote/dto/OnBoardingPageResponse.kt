package com.septianbeneran.urithiru.api.b.data.remote.dto

import kotlinx.serialization.SerialName
import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import kotlinx.serialization.Serializable

@Serializable
data class OnBoardingPageResponse (
    @SerialName("image_url") val imgUrl: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String
) {
    fun mapToEntity() = OnBoardingPageData(
        imgUrl = imgUrl,
        title = title,
        description = description
    )
}