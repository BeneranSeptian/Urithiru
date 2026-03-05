package com.septianbeneran.urithiru.core.entity.b

data class Boss(
    val id: String,
    val name: String,
    val image: String?,
    val description: String?,
    val location: String?,
    val drops: List<String>,
    val healthPoints: String?
)