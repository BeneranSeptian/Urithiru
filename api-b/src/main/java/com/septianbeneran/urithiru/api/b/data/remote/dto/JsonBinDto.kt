package com.septianbeneran.urithiru.api.b.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JsonBinDto<T> (
    val record: T,
    val metadata: JsonBinMetaData
) {
    @Serializable
    data class JsonBinMetaData(
        val id: String?,
        val private: Boolean?,
        val createdAt: String?,
        val name: String?
    )
}