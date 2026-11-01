package com.septianbeneran.urithiru.core.ui.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.septianbeneran.urithiru.core.localization.AssetTranslator
import com.septianbeneran.urithiru.core.localization.Translator
import com.septianbeneran.urithiru.core.ui.R
import kotlinx.serialization.json.Json
import java.util.Locale

/** The app's [Translator], provided by `MainActivity`. Previews fall back to reading the bundled file. */
val LocalTranslator = staticCompositionLocalOf<Translator?> { null }

/**
 * The text for a string resource. The resource's value is a localization key (`R.string.common_next` →
 * `commonNext`), translated through the translation JSON into the current language. [args] fill
 * `{0}`, `{1}`, … in order; a count also picks the plural form.
 */
@Composable
fun localizedString(@StringRes id: Int, vararg args: Any): String {
    val key = stringResource(id)
    val locale = LocalConfiguration.current.locales[0]
    return currentTranslator().translate(key, *args, locale = locale)
}

/** The text for a localization key that didn't come from a string resource, e.g. one sent by a backend. */
@Composable
fun localizedString(key: String, args: Map<String, Any> = emptyMap()): String {
    val locale = LocalConfiguration.current.locales[0]
    return currentTranslator().translate(key, args, locale)
}

/** The language tags the translation file has text for, in file order. */
@Composable
fun availableLanguages(): List<String> = currentTranslator().languages

/** The name of the language [languageTag] in that language itself, e.g. `id` → "Bahasa Indonesia". */
@Composable
fun languageName(languageTag: String): String =
    currentTranslator().translate(stringResource(R.string.language_name), locale = Locale.forLanguageTag(languageTag))

@Composable
private fun currentTranslator(): Translator {
    val provided = LocalTranslator.current
    val context = LocalContext.current
    return provided ?: remember(context) {
        AssetTranslator(context.applicationContext, Json { ignoreUnknownKeys = true })
    }
}
