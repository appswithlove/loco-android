package com.appswithlove.loco.http

import com.appswithlove.loco.plugin.LocoConfig
import java.io.IOException

internal class FakeLocoHttpClient : LocoHttpClient {
    var localesResponse: String = "[]"
    var translationResponses: MutableMap<String, String> = mutableMapOf()
    var pushTranslationResponse: String =
        """{"message":"3 assets imported","locales":[{"progress":{"translated":3,"untranslated":0}}]}"""
    val capturedPushLocales: MutableList<String> = mutableListOf()
    val capturedPushBodies: MutableList<String> = mutableListOf()
    var shouldFail = false
    var failureException: Exception = IOException("Fake network error")

    override fun fetchLocales(apiKey: String): String {
        if (shouldFail) throw failureException
        return localesResponse
    }

    override fun fetchTranslation(locoConfig: LocoConfig, language: String): String {
        if (shouldFail) throw failureException
        return translationResponses[language] ?: "<resources></resources>"
    }

    override fun pushTranslation(
        apiKey: String,
        locale: String,
        xmlContent: String,
        importBaseUrl: String,
    ): String {
        if (shouldFail) throw failureException
        capturedPushLocales.add(locale)
        capturedPushBodies.add(xmlContent)
        return pushTranslationResponse
    }
}
