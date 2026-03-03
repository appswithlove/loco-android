package com.appswithlove.loco.util

import com.appswithlove.loco.dto.LocaleDto
import com.appswithlove.loco.http.DefaultLocoHttpClient
import com.appswithlove.loco.http.LocoHttpClient
import com.appswithlove.loco.plugin.LocoConfig
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import java.io.File

object TaskUtils {
    internal fun generate(
        locoConfig: LocoConfig,
        httpClient: LocoHttpClient = DefaultLocoHttpClient(),
    ) {
        val languages: List<String> = locoConfig.lang?.takeIf { it.isNotEmpty() } ?: run {
            println("Languages are not specified in Loco config. Fetching all languages from the project.")
            fetchAllLanguages(httpClient, locoConfig.apiKey).ifEmpty { emptyList() }
        }

        for (langEntry in languages) {
            var lang = langEntry
            var text = httpClient.fetchTranslation(locoConfig, lang)

            // Replace placeholders
            locoConfig.placeholderPattern?.let { pattern ->
                text = text.replace(Regex(pattern), "%s")
            }

            // Add resource name prefix if specified
            locoConfig.resourceNamePrefix?.let { prefix ->
                text = text
                    .replace("<string name=\"", "<string name=\"$prefix")
                    .replace("<plurals name=\"", "<plurals name=\"$prefix")
            }

            // Certain languages have multiple regions (e.g., Spanish (Spain) and Spanish (Mexico)),
            // and their folder titles have an additional "r" in them.
            if (lang.contains("-")) {
                lang = lang.replace("-", "-r")
            }
            val appendix = "-$lang"

            // Replace given keys with values in the text
            if (locoConfig.replace.isNotEmpty()) {
                locoConfig.replace.forEach { (key, value) ->
                    text = text.replace(Regex(key), value)
                }
            }

            // In some rare cases, the encoding parameter in the xml-tag is 'utf8' instead of 'utf-8'
            val wrongXmlString = """<?xml version="1.0" encoding="utf8"?>"""
            if (text.startsWith(wrongXmlString)) {
                val expectedXmlString = """<?xml version="1.0" encoding="utf-8"?>"""
                text = text.replaceFirst(wrongXmlString, expectedXmlString)
            }

            if (lang == locoConfig.defLang) {
                if (locoConfig.ignoreMissingTranslationWarnings) {
                    text = text.replaceFirst(
                        "<resources",
                        """<resources xmlns:tools="http://schemas.android.com/tools" tools:ignore="MissingTranslation" """
                    )
                }
                saveFile(locoConfig, text)
            }

            if (lang != locoConfig.defLang || locoConfig.saveDefLangDuplicate) {
                saveFile(locoConfig, text, appendix)
            }
        }
    }

    internal fun fetchAllLanguages(httpClient: LocoHttpClient, apiKey: String?): List<String> {
        if (apiKey != null) {
            try {
                val response = httpClient.fetchLocales(apiKey)
                val json = Json { ignoreUnknownKeys = true }
                return json.decodeFromString<List<LocaleDto>>(response).map { it.code }
            } catch (e: Exception) {
                throw GradleException("Error fetching languages: ${e.message}")
            }
        } else {
            throw GradleException("Can't fetch languages. API key is missing in Loco config.")
        }
    }

    private fun saveFile(locoConfig: LocoConfig, text: String, appendix: String = "") {
        val directory = File("${locoConfig.resDir}/values$appendix/")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory.absolutePath, "${locoConfig.fileName}.xml")
        file.writeText(text, Charsets.UTF_8)
    }
}
