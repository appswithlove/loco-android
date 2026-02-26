package com.appswithlove.loco.util

import com.appswithlove.loco.http.FakeLocoHttpClient
import com.appswithlove.loco.plugin.LocoConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.api.GradleException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException

class TaskUtilsTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fake: FakeLocoHttpClient

    @BeforeEach
    fun setUp() {
        fake = FakeLocoHttpClient()
    }

    private fun config(block: LocoConfig.() -> Unit = {}): LocoConfig = LocoConfig().apply {
        apiKey = "test-api-key"
        resDir = tempDir.absolutePath
        defLang = "en"
        lang = listOf("en")
        block()
    }

    @Test
    fun generate_withValidConfig_createsXmlFile() {
        fake.translationResponses["en"] =
            "<resources><string name=\"hello\">Hello</string></resources>"

        TaskUtils.generate(config(), fake)

        File(tempDir, "values/strings.xml").exists() shouldBe true
    }

    @Test
    fun generate_withMultipleLanguages_createsAllFiles() {
        fake.translationResponses["en"] = "<resources></resources>"
        fake.translationResponses["de"] = "<resources></resources>"
        fake.translationResponses["fr"] = "<resources></resources>"

        TaskUtils.generate(config { lang = listOf("en", "de", "fr") }, fake)

        File(tempDir, "values/strings.xml").exists() shouldBe true
        File(tempDir, "values-de/strings.xml").exists() shouldBe true
        File(tempDir, "values-fr/strings.xml").exists() shouldBe true
    }

    @Test
    fun generate_withNetworkError_propagatesException() {
        fake.shouldFail = true
        fake.failureException = IOException("Timeout")

        shouldThrow<IOException> {
            TaskUtils.generate(config(), fake)
        }
    }

    @Test
    fun generate_withDefLang_savesFileInBaseValuesDirectory() {
        fake.translationResponses["en"] = "<resources></resources>"

        TaskUtils.generate(config { defLang = "en"; lang = listOf("en") }, fake)

        File(tempDir, "values/strings.xml").exists() shouldBe true
        File(tempDir, "values-en/strings.xml").exists() shouldBe false
    }

    @Test
    fun generate_withNonDefLang_savesFileInLanguageSpecificDirectory() {
        fake.translationResponses["de"] = "<resources></resources>"

        TaskUtils.generate(config { lang = listOf("de") }, fake)

        File(tempDir, "values-de/strings.xml").exists() shouldBe true
        File(tempDir, "values/strings.xml").exists() shouldBe false
    }

    @Test
    fun generate_withRegionalLanguage_convertsToAndroidFormat() {
        fake.translationResponses["es-MX"] = "<resources></resources>"

        TaskUtils.generate(config { lang = listOf("es-MX") }, fake)

        File(tempDir, "values-es-rMX/strings.xml").exists() shouldBe true
        File(tempDir, "values-es-MX/strings.xml").exists() shouldBe false
        File(tempDir, "values-es/strings.xml").exists() shouldBe false
        File(tempDir, "values/strings.xml").exists() shouldBe false
    }

    @Test
    fun generate_withPlaceholderPattern_replacesPlaceholders() {
        fake.translationResponses["en"] =
            "<resources><string name=\"msg\">Hello {name}</string></resources>"

        TaskUtils.generate(config { placeholderPattern = "\\{[^}]+\\}" }, fake)

        File(tempDir, "values/strings.xml").readText() shouldContain "%s"
        File(tempDir, "values/strings.xml").readText() shouldNotContain "{name}"
    }

    @Test
    fun generate_withResourceNamePrefix_addsPrefix() {
        fake.translationResponses["en"] =
            "<resources><string name=\"hello\">Hello</string></resources>"

        TaskUtils.generate(config { resourceNamePrefix = "app_" }, fake)

        File(tempDir, "values/strings.xml").readText() shouldContain "app_hello"
        File(tempDir, "values/strings.xml").readText() shouldNotContain "\"hello\""
    }

    @Test
    fun generate_withUtf8EncodingBug_fixesEncoding() {
        fake.translationResponses["en"] =
            """<?xml version="1.0" encoding="utf8"?><resources></resources>"""

        TaskUtils.generate(config(), fake)

        File(tempDir, "values/strings.xml").readText() shouldContain """encoding="utf-8""""
    }

    @Test
    fun fetchAllLanguages_parsesJsonCorrectly() {
        fake.localesResponse = """[{"code":"en"},{"code":"de"},{"code":"fr"}]"""

        val result = TaskUtils.fetchAllLanguages(fake, "test-key")

        result shouldContainExactlyInAnyOrder listOf("en", "de", "fr")
    }

    @Test
    fun fetchAllLanguages_withEmptyResponse_returnsEmptyList() {
        fake.localesResponse = "[]"

        val result = TaskUtils.fetchAllLanguages(fake, "test-key")

        result shouldBe emptyList()
    }

    @Test
    fun fetchAllLanguages_withNullApiKey_throwsGradleException() {
        shouldThrow<GradleException> {
            TaskUtils.fetchAllLanguages(fake, null)
        }
    }

    @Test
    fun generate_withSaveDefLangDuplicate_savesFilesInBothDirectories() {
        fake.translationResponses["en"] = "<resources></resources>"

        TaskUtils.generate(config { saveDefLangDuplicate = true }, fake)

        File(tempDir, "values/strings.xml").exists() shouldBe true
        File(tempDir, "values-en/strings.xml").exists() shouldBe true
    }

    @Test
    fun generate_withIgnoreMissingTranslationWarnings_addsXmlNamespace() {
        fake.translationResponses["en"] = "<resources><string name=\"a\">A</string></resources>"

        TaskUtils.generate(config { ignoreMissingTranslationWarnings = true }, fake)

        File(tempDir, "values/strings.xml").readText() shouldContain "tools:ignore=\"MissingTranslation\""
    }

    @Test
    fun generate_withReplaceMap_replacesTextInOutput() {
        fake.translationResponses["en"] = "<resources><string name=\"a\">Hello World</string></resources>"

        TaskUtils.generate(config { replace = mapOf("World" to "Earth") }, fake)

        File(tempDir, "values/strings.xml").readText() shouldContain "Earth"
        File(tempDir, "values/strings.xml").readText() shouldNotContain "World"
    }

    @Test
    fun generate_withNoLangConfigured_fetchesLanguagesFromApi() {
        fake.localesResponse = """[{"code":"en"},{"code":"de"}]"""
        fake.translationResponses["en"] = "<resources></resources>"
        fake.translationResponses["de"] = "<resources></resources>"

        TaskUtils.generate(config { lang = null }, fake)

        File(tempDir, "values/strings.xml").exists() shouldBe true
        File(tempDir, "values-de/strings.xml").exists() shouldBe true
    }
}
