package com.septianbeneran.urithiru.core.localization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class TranslationCatalogTest {

    private val catalog = TranslationCatalog(
        TranslationFile(
            version = 3,
            defaultLanguage = "en",
            languages = mapOf(
                "en" to mapOf("greeting" to "Hello", "onlyEnglish" to "English only"),
                "id" to mapOf("greeting" to "Halo"),
                "pt" to mapOf("greeting" to "Olá"),
                "pt-BR" to mapOf("greeting" to "Oi")
            )
        )
    )

    @Test
    fun `uses the language of the locale`() {
        assertEquals("Halo", catalog.pattern("greeting", Locale.forLanguageTag("id-ID")))
    }

    @Test
    fun `prefers the exact language tag over the bare language`() {
        assertEquals("Oi", catalog.pattern("greeting", Locale.forLanguageTag("pt-BR")))
        assertEquals("Olá", catalog.pattern("greeting", Locale.forLanguageTag("pt-PT")))
    }

    @Test
    fun `falls back to the default language when the key is missing`() {
        assertEquals("English only", catalog.pattern("onlyEnglish", Locale.forLanguageTag("id")))
    }

    @Test
    fun `falls back to the default language for an unknown language`() {
        assertEquals("Hello", catalog.pattern("greeting", Locale.forLanguageTag("ja-JP")))
    }

    @Test
    fun `returns null for a key no language has`() {
        assertNull(catalog.pattern("unknown", Locale.ENGLISH))
    }

    @Test
    fun `exposes the file version`() {
        assertEquals(3, catalog.version)
    }
}
