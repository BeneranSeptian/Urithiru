package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

/** Trims a text field and turns the API's placeholders ("", "???", "None") into null. */
internal fun String?.cleaned(): String? =
    this?.trim()?.takeUnless { it.isEmpty() || it == "???" || it.equals("None", ignoreCase = true) }
