package com.septianbeneran.urithiru.api.b.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import kotlinx.serialization.Serializable

@Serializable
data class OnBoardingPageResponse (
    @SerializedName("image_url") val imgUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
) {
    fun mapToEntity() = OnBoardingPageData(
        imgUrl = imgUrl,
        title = title,
        description = description
    )
}