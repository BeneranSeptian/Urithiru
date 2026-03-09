package com.septianbeneran.urithiru.api.b.data.remote.dto

data class JsonBinDto<T> (
    val record: T,
    val metadata: JsonBinMetaData
) {
    data class JsonBinMetaData(
        val id: String?,
        val private: Boolean?,
        val createdAt: String?,
        val name: String?
    )
}