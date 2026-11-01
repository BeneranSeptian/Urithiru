package com.septianbeneran.urithiru.core.localization

import java.util.Locale

/**
 * Turns a localization key (e.g. `commonTitleCatalog`) into text in the user's language, using the
 * translation JSON. Text is ICU MessageFormat: `{0}` for arguments, `{0, plural, one {...} other {...}}`
 * for counts. Anything that isn't a key in the file (plain text, or a misspelled key) comes back exactly as
 * given, unformatted, so backend text works as-is and a wrong key is easy to spot on screen.
 *
 * In Compose use `localizedString(R.string.x)` from `:core-ui`; inject this where there's no composition.
 */
interface Translator {
    /** The version of the translation file in use. It's picked once per launch. */
    val version: Int

    /** The language used when the user hasn't picked one, and for keys missing in the chosen language. */
    val defaultLanguage: String

    /** The language tags the translation file has text for, e.g. `en`, `id`. */
    val languages: List<String>

    /** Translates [text] if it's a key, filling `{0}`, `{1}`, … with [args] in order; otherwise returns it as-is. */
    fun translate(text: String, vararg args: Any, locale: Locale = Locale.getDefault()): String

    /** Translates [text] if it's a key, filling named placeholders such as `{billerName}` from [args]; otherwise returns it as-is. */
    fun translate(text: String, args: Map<String, Any>, locale: Locale = Locale.getDefault()): String
}
