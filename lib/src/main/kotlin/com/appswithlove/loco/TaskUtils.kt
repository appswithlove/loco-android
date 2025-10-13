package com.appswithlove.loco

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

        for (langEntry in locoConfig.lang) {
            var lang = langEntry
            val baseUrl = URL("${locoConfig.locoBaseUrl}/$lang.xml$parameter")

            val connection = baseUrl.openConnection() as HttpURLConnection
            connection.addRequestProperty("Authorization", "Loco ${locoConfig.apiKey}")
            connection.addRequestProperty("Accept-Charset", "utf-8")

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
}
