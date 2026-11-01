package com.septianbeneran.urithiru.core.remote.entity

import kotlinx.serialization.Serializable

/** The envelope list and detail endpoints wrap their data in. [total] is the size of the whole collection when the API reports it. */
@Serializable
data class ApiDto<out T>(
    val success: Boolean?,
    val count: Int?,
    val total: Int? = null,
    val data: T
)
