package com.appswithlove.loco.http

import com.appswithlove.loco.plugin.LocoConfig
import java.io.IOException

internal class FakeLocoHttpClient : LocoHttpClient {
    var localesResponse: String = "[]"
    var translationResponses: MutableMap<String, String> = mutableMapOf()
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
}
