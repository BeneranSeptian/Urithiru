package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

/** Trims a text field and turns the API's placeholders ("", "???", "None") into null. */
internal fun String?.cleaned(): String? =
    this?.trim()?.takeUnless { it.isEmpty() || it == "???" || it.equals("None", ignoreCase = true) }

private val RegionSpellings = mapOf(
    "Altus Pleateau" to "Altus Plateau",
    "Liunia of the Lakes" to "Liurnia of the Lakes",
    "Mountaintop of the Giants" to "Mountaintops of the Giants"
)

/** Like [cleaned], and also fixes the misspelled region names the API uses for some entries. */
internal fun String?.cleanedRegion(): String? =
    cleaned()?.let { RegionSpellings[it] ?: it }
