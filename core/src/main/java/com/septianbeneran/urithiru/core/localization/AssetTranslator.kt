package com.septianbeneran.urithiru.core.localization

import android.content.Context
import android.icu.text.MessageFormat
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads the translations once per process: the bundled `assets/localization/translations.json`, or a
 * newer downloaded copy in `filesDir/localization/translations.json` if one has a higher version. A file
 * downloaded during a session is only picked up on the next launch, so text never changes mid-session.
 */
@Singleton
class AssetTranslator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : Translator {

    private val catalog by lazy { TranslationCatalog(load()) }

    override val version: Int get() = catalog.version

    override val defaultLanguage: String get() = catalog.defaultLanguage

    override val languages: List<String> get() = catalog.languages

    override fun translate(text: String, vararg args: Any, locale: Locale): String {
        val pattern = catalog.pattern(text, locale) ?: return untranslated(text)
        return if (args.isEmpty()) pattern else format(pattern, args, locale)
    }

    override fun translate(text: String, args: Map<String, Any>, locale: Locale): String {
        val pattern = catalog.pattern(text, locale) ?: return untranslated(text)
        return if (args.isEmpty()) pattern else format(pattern, args, locale)
    }

    private fun format(pattern: String, args: Any, locale: Locale): String = try {
        MessageFormat(pattern, locale).format(args)
    } catch (e: IllegalArgumentException) {
        Log.w(TAG, "Invalid translation pattern: $pattern", e)
        pattern
    }

    private fun load(): TranslationFile =
        listOfNotNull(readDownloaded(), readBundled()).maxBy { it.version }

    private fun readBundled(): TranslationFile =
        context.assets.open(BUNDLED_PATH).bufferedReader().use { json.decodeFromString(it.readText()) }

    private fun readDownloaded(): TranslationFile? {
        val file = File(context.filesDir, DOWNLOADED_PATH)
        if (!file.exists()) return null
        return runCatching { json.decodeFromString<TranslationFile>(file.readText()) }
            .onFailure { Log.w(TAG, "Ignoring an unreadable downloaded translation file", it) }
            .getOrNull()
    }

    /** Plain text passes through quietly; something shaped like a key (no spaces) is logged as a likely typo. */
    private fun untranslated(text: String): String {
        if (text.isNotBlank() && text.none { it.isWhitespace() }) Log.w(TAG, "No translation for key: $text")
        return text
    }

    companion object {
        const val BUNDLED_PATH = "localization/translations.json"
        const val DOWNLOADED_PATH = "localization/translations.json"
        private const val TAG = "Translator"
    }
}
