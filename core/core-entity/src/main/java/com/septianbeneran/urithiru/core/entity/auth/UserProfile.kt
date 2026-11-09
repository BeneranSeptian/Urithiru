package com.septianbeneran.urithiru.core.entity.auth

data class UserProfile(
    val user: User,
    val phone: String?,
    val birthDate: String?,
    val age: Int?,
    val gender: String?,
    val jobTitle: String?,
    val company: String?,
    val city: String?,
    val country: String?
)
