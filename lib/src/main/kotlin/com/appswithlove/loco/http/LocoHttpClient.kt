package com.appswithlove.loco.http

import com.appswithlove.loco.plugin.LocoConfig

internal interface LocoHttpClient {
    fun fetchLocales(apiKey: String): String
    fun fetchTranslation(locoConfig: LocoConfig, language: String): String
}
