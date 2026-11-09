package com.septianbeneran.urithiru.core.localization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The translation JSON: `{"version": 1, "defaultLanguage": "en", "languages": {"en": {...}, "id": {...}}}`.
 * Languages are BCP 47 tags (`en`, `id`, `pt-BR`); each maps localization keys to ICU MessageFormat text.
 */
@Serializable
data class TranslationFile(
    @SerialName("version") val version: Int,
    @SerialName("defaultLanguage") val defaultLanguage: String,
    @SerialName("languages") val languages: Map<String, Map<String, String>>
)
