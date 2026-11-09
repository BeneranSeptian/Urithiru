package com.septianbeneran.urithiru.core.localization

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/** Checks the bundled translation file against every module's strings.xml. */
class TranslationFileTest {

    private val rootDir = File("../..").canonicalFile
    private val file: TranslationFile = Json.decodeFromString(
        File("src/main/assets/localization/${AssetTranslator.BUNDLED_PATH.substringAfter('/')}").readText()
    )
    /** Modules sit at the root (`app`) or one level down in a cluster folder (`core/core-ui`, `feature/feature-shop`). */
    private val resourceKeys: Map<String, String> = rootDir
        .walk().maxDepth(2)
        .filter { it.isDirectory }
        .map { File(it, "src/main/res/values/strings.xml") }
        .filter { it.exists() }
        .flatMap { stringValues(it) }
        .toMap()

    @Test
    fun `every string resource has a translation in every language`() {
        assertTrue("No string resources found", resourceKeys.isNotEmpty())
        file.languages.forEach { (language, translations) ->
            val missing = resourceKeys.values.filter { it !in translations }
            assertTrue("Missing in $language: $missing", missing.isEmpty())
        }
    }

    @Test
    fun `every language has the same keys as the default language`() {
        val expected = file.languages.getValue(file.defaultLanguage).keys
        file.languages.forEach { (language, translations) ->
            assertEquals("Keys differ in $language", expected, translations.keys)
        }
    }

    @Test
    fun `no translation key is unused by the string resources`() {
        val unused = file.languages.getValue(file.defaultLanguage).keys - resourceKeys.values.toSet()
        assertTrue("Not in any strings.xml: $unused", unused.isEmpty())
    }

    @Test
    fun `every language uses the same arguments as the default language`() {
        val default = file.languages.getValue(file.defaultLanguage)
        file.languages.forEach { (language, translations) ->
            translations.forEach { (key, text) ->
                assertEquals("Arguments differ for $key in $language", arguments(default.getValue(key)), arguments(text))
            }
        }
    }

    @Test
    fun `string resource values are their camelCase names`() {
        resourceKeys.forEach { (name, value) ->
            assertEquals("Value of $name", camelCase(name), value)
        }
    }

    private fun stringValues(xml: File): List<Pair<String, String>> {
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml).getElementsByTagName("string")
        return (0 until nodes.length).map { index ->
            val node = nodes.item(index)
            node.attributes.getNamedItem("name").nodeValue to node.textContent.trim()
        }
    }

    private fun arguments(text: String): Set<String> {
        val withoutBranches = generateSequence(text) { it.replace(PluralBranch, "") }
            .zipWithNext().first { (before, after) -> before == after }.first
        return Argument.findAll(withoutBranches).map { it.groupValues[1] }.toSet()
    }

    private fun camelCase(name: String): String =
        name.split('_').mapIndexed { index, part -> if (index == 0) part else part.replaceFirstChar(Char::uppercase) }.joinToString("")

    private companion object {
        val PluralBranch = Regex("""(zero|one|two|few|many|other|=\d+)\s*\{[^{}]*\}""")
        val Argument = Regex("""\{\s*(\w+)\s*[,}]""")
    }
}
