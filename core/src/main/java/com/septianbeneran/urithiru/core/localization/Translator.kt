package com.septianbeneran.urithiru.core.localization

import java.util.Locale

/**
 * Turns a localization key (e.g. `commonTitleCatalog`) into text in the user's language, using the
 * translation JSON. Text is ICU MessageFormat: `{0}` for arguments, `{0, plural, one {...} other {...}}`
 * for counts. A key with no translation in any language comes back unchanged.
 *
 * In Compose use `localizedString(R.string.x)` from `:core-ui`; inject this where there's no composition.
 */
interface Translator {
    /** The version of the translation file in use. It's picked once per launch. */
    val version: Int

    /** Translates [key], filling `{0}`, `{1}`, … with [args] in order. */
    fun translate(key: String, vararg args: Any, locale: Locale = Locale.getDefault()): String

    /** Translates [key], filling named placeholders such as `{billerName}` from [args]. */
    fun translate(key: String, args: Map<String, Any>, locale: Locale = Locale.getDefault()): String
}
