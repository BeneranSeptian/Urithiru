package com.septianbeneran.urithiru.core.util.extension

/** Whole numbers without decimals ("7"), anything else with one decimal ("6.6"). */
fun Float.formatted(): String =
    if (this % 1f == 0f) toInt().toString() else "%.1f".format(this)
