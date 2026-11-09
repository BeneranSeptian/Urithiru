package com.septianbeneran.urithiru.core.entity.eldenring.common

/** A named stat that can be fractional, like armor damage negation (`Phy 2.1`). */
data class StatValue(
    val name: String,
    val amount: Float
)
