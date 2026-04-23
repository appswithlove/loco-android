package com.appswithlove.loco.http

import com.appswithlove.loco.Constants
import com.appswithlove.loco.plugin.LocoConfig
import java.net.HttpURLConnection
import java.net.URL

internal class DefaultLocoHttpClient : LocoHttpClient {

    override fun fetchLocales(apiKey: String): String {
        val connection =
            (URL(Constants.LOCO_LOCALES_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Loco $apiKey")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 10000
                readTimeout = 10000
            }
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    override fun fetchTranslation(locoConfig: LocoConfig, language: String): String {
        var parameter = "?no-comments=${locoConfig.hideComments}"
        locoConfig.tags?.let { parameter += "&filter=$it" }
        locoConfig.index?.let { parameter += "&index=$it" }
        locoConfig.status?.let { parameter += "&status=$it" }
        locoConfig.fallbackLang?.let { parameter += "&fallback=$it" }
        if (locoConfig.orderByAssetId) parameter += "&order=id"

        val connection =
            (URL("${locoConfig.locoBaseUrl}/$language.xml$parameter").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                addRequestProperty("Authorization", "Loco ${locoConfig.apiKey}")
                addRequestProperty("Accept-Charset", "utf-8")
                connectTimeout = 10000
                readTimeout = 10000
            }

        if (connection.responseCode != 200) {
            val error = (connection.errorStream ?: connection.inputStream).bufferedReader()
                .use { it.readText() }
            throw IllegalStateException("Request failed with code ${connection.responseCode}: $error")
        }

        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    override fun pushTranslation(
        apiKey: String,
        locale: String,
        xmlContent: String,
        importBaseUrl: String,
    ): String {
        val bytes = xmlContent.toByteArray(Charsets.UTF_8)
        val connection =
            (URL("$importBaseUrl/xml?locale=$locale&delete-absent=0").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Authorization", "Loco $apiKey")
                setRequestProperty("Content-Type", "text/xml; charset=utf-8")
                connectTimeout = 10000
                readTimeout = 10000
            }
        connection.outputStream.use { it.write(bytes) }

        if (connection.responseCode != 200) {
            val error = (connection.errorStream ?: connection.inputStream)
                .bufferedReader()
                .use { it.readText() }
            throw IllegalStateException("Push failed with code ${connection.responseCode}: $error")
        }

        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
