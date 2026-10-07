package com.septianbeneran.urithiru.core.remote.entity

import kotlinx.serialization.Serializable

@Serializable
data class ApiDto<T>(
    val success: Boolean?,
    val count: Int?,
    val data: T
)