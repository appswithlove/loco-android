package com.appswithlove.loco.util

import com.appswithlove.loco.Constants
import com.appswithlove.loco.dto.LocaleDto
import com.appswithlove.loco.plugin.LocoConfig
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Helper to generate XML file from a loco config
 */
object TaskUtils {

    fun generate(locoConfig: LocoConfig) {
        // Determine whether it should show comments or not
        var parameter = "?no-comments=${locoConfig.hideComments}"

        // Add tags filter
        locoConfig.tags?.let {
            parameter += "&filter=$it"
        }

        // Add index
        locoConfig.index?.let {
            parameter += "&index=$it"
        }

        // Add status filter
        locoConfig.status?.let {
            parameter += "&status=$it"
        }

        // If a fallback language was specified, include it as a parameter
        locoConfig.fallbackLang?.let {
            parameter += "&fallback=$it"
        }

        // Determine whether assets should be ordered alphabetically by Asset ID, include parameter if necessary
        if (locoConfig.orderByAssetId) {
            parameter += "&order=id"
        }

        val languages: List<String> = locoConfig.lang?.takeIf { it.isNotEmpty() } ?: run {
            println("Languages are not specified in Loco config. Fetching all languages from the project.")
            val response = fetchAllLanguages(locoConfig.apiKey)
            response.ifEmpty { emptyList() }
        }

        for (langEntry in languages) {
            var lang = langEntry
            val baseUrl = URL("${locoConfig.locoBaseUrl}/$lang.xml$parameter")

            val connection = baseUrl.openConnection() as HttpURLConnection
            connection.addRequestProperty("Authorization", "Loco ${locoConfig.apiKey}")
            connection.addRequestProperty("Accept-Charset", "utf-8")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true
            connection.requestMethod = "GET"

            var text = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

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
            // todo: if there's a better, more reliable solution to handle this, please submit a PR.
            val wrongXmlString = """<?xml version="1.0" encoding="utf8"?>"""
            if (text.startsWith(wrongXmlString)) {
                val expectedXmlString = """<?xml version="1.0" encoding="utf-8"?>"""
                text = text.replaceFirst(wrongXmlString, expectedXmlString)
            }

            if (lang == locoConfig.defLang) {
                // Ignore missing translation warnings for every string inside the default strings file
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

    private fun saveFile(locoConfig: LocoConfig, text: String, appendix: String = "") {
        val directory = File("${locoConfig.resDir}/values$appendix/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath, "${locoConfig.fileName}.xml")
        file.writeText(text, Charsets.UTF_8)
    }

    fun fetchAllLanguages(apiKey: String?): List<String> {
        if (apiKey != null) {
            try {
                val url = URL(Constants.LOCO_LOCALES_URL)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Loco $apiKey")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 10000
                    readTimeout = 10000
                }

                val response =
                    connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

                val json = Json { ignoreUnknownKeys = true }
                return json.decodeFromString<List<LocaleDto>>(response).map { it.code }
            } catch (e: Exception) {
                throw GradleException("Error fetching languages: ${e.message}")
            }
        } else {
            throw GradleException("Can't fetch languages. API key is missing in Loco config.")
        }
    }
}
