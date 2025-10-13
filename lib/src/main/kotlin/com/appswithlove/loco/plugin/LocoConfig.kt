package com.appswithlove.loco.plugin

import com.appswithlove.loco.Constants

class LocoConfig {
    var locoBaseUrl: String = Constants.LOCO_EXPORT_LOCALE_URL
    var apiKey: String? = null
    var lang: List<String>? = null
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
