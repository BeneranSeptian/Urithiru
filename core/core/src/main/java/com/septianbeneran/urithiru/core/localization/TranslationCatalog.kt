package com.septianbeneran.urithiru.core.localization

import java.util.Locale

/** Finds the text for a key: the exact language tag (`pt-BR`), then its language (`pt`), then the default. */
class TranslationCatalog(private val file: TranslationFile) {

    val version: Int get() = file.version

    val defaultLanguage: String get() = file.defaultLanguage

    val languages: List<String> get() = file.languages.keys.toList()

    fun pattern(key: String, locale: Locale): String? =
        languagesFor(locale).firstNotNullOfOrNull { file.languages[it]?.get(key) }

    private fun languagesFor(locale: Locale): List<String> {
        val tag = locale.toLanguageTag()
        return listOf(tag, tag.substringBefore('-'), file.defaultLanguage).distinct()
    }
}
