package com.septianbeneran.urithiru.api.dummyjson.data.remote.dto

import com.septianbeneran.urithiru.core.entity.auth.User
import com.septianbeneran.urithiru.core.entity.auth.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("image") val image: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("company") val company: CompanyDto? = null,
    @SerialName("address") val address: AddressDto? = null
) {
    @Serializable
    data class CompanyDto(
        @SerialName("name") val name: String? = null,
        @SerialName("title") val title: String? = null
    )

    @Serializable
    data class AddressDto(
        @SerialName("city") val city: String? = null,
        @SerialName("country") val country: String? = null
    )

    fun mapToEntity() = UserProfile(
        user = User(
            id = id,
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            image = image
        ),
        phone = phone,
        birthDate = birthDate,
        age = age,
        gender = gender,
        jobTitle = company?.title,
        company = company?.name,
        city = address?.city,
        country = address?.country
    )
}
