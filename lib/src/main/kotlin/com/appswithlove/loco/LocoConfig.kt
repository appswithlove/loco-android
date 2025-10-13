package com.appswithlove.loco

class LocoConfig {
    var locoBaseUrl: String = "https://localise.biz/api/export/locale"
    var apiKey: String? = null
    var lang: List<String> = listOf("en")
    var defLang: String = "en"
    var resDir: String? = null
    var fileName: String = "strings"
    var replace: Map<String, String> = emptyMap()
    var placeholderPattern: String? = null
    var hideComments: Boolean = false
    var tags: List<String>? = null
    var fallbackLang: String? = null
    var orderByAssetId: Boolean = false
    var status: String? = null
    var saveDefLangDuplicate: Boolean = false
    var resourceNamePrefix: String? = null
    var ignoreMissingTranslationWarnings: Boolean = false
    var index: String? = null
}
