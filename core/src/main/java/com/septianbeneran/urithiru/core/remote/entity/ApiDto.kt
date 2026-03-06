package com.septianbeneran.urithiru.core.remote.entity


data class ApiDto<T>(
    val success: Boolean?,
    val count: Int?,
    val data: T
)