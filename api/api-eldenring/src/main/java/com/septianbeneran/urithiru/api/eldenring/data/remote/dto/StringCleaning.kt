package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.util.extension.cleaned

private val RegionSpellings = mapOf(
    "Altus Pleateau" to "Altus Plateau",
    "Liunia of the Lakes" to "Liurnia of the Lakes",
    "Mountaintop of the Giants" to "Mountaintops of the Giants"
)

/** Like [cleaned], and also fixes the misspelled region names the API uses for some entries. */
internal fun String?.cleanedRegion(): String? =
    cleaned()?.let { RegionSpellings[it] ?: it }
