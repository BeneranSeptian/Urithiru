package com.septianbeneran.urithiru.core.util.extension

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** Trims the text and turns blanks and API placeholders ("???", "None") into null. */
fun String?.cleaned(): String? =
    this?.trim()?.takeUnless { it.isEmpty() || it == "???" || it.equals("None", ignoreCase = true) }

/** Shortens the text to [maxLength] by cutting out its middle, e.g. "https://api…/weapons". */
fun String.ellipsizeCenter(maxLength: Int): String {
    if (length <= maxLength) return this

    val halfLength = (maxLength - 5) / 2
    return "${substring(0, halfLength)} ... ${substring(length - halfLength)}"
}

/** Indents the text when it is a JSON object or array; anything else comes back unchanged. */
fun String.asPrettyJson(): String {
    val trimmed = trim()
    return try {
        when {
            trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
            trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
            else -> this
        }
    } catch (_: JSONException) {
        this
    }
}
