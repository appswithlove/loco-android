package com.appswithlove.loco.plugin

import com.appswithlove.loco.Constants
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class LocoConfigTest {

    @Test
    fun config_defaultValues_areCorrect() {
        val config = LocoConfig()

        config.locoBaseUrl shouldBe Constants.LOCO_EXPORT_LOCALE_URL
        config.apiKey shouldBe null
        config.lang shouldBe null
        config.defLang shouldBe null
        config.resDir shouldBe null
        config.fileName shouldBe "strings"
        config.replace shouldBe emptyMap()
        config.placeholderPattern shouldBe null
        config.hideComments shouldBe false
        config.tags shouldBe null
        config.fallbackLang shouldBe null
        config.orderByAssetId shouldBe false
        config.status shouldBe null
        config.saveDefLangDuplicate shouldBe false
        config.resourceNamePrefix shouldBe null
        config.ignoreMissingTranslationWarnings shouldBe false
        config.index shouldBe null
    }

    @Test
    fun config_canSetAllProperties() {
        val config = LocoConfig().apply {
            apiKey = "apiKey"
            lang = listOf("en", "de")
            defLang = "en"
            resDir = "/tmp/res"
            fileName = "translations"
            replace = mapOf("old" to "new")
            placeholderPattern = "\\{[^}]+\\}"
            hideComments = true
            tags = "android"
            fallbackLang = "en"
            orderByAssetId = true
            status = "translated"
            saveDefLangDuplicate = true
            resourceNamePrefix = "app_"
            ignoreMissingTranslationWarnings = true
            index = "name"
        }

        config.apiKey shouldBe "apiKey"
        config.lang shouldBe listOf("en", "de")
        config.defLang shouldBe "en"
        config.resDir shouldBe "/tmp/res"
        config.fileName shouldBe "translations"
        config.replace shouldBe mapOf("old" to "new")
        config.placeholderPattern shouldBe "\\{[^}]+\\}"
        config.hideComments shouldBe true
        config.tags shouldBe "android"
        config.fallbackLang shouldBe "en"
        config.orderByAssetId shouldBe true
        config.status shouldBe "translated"
        config.saveDefLangDuplicate shouldBe true
        config.resourceNamePrefix shouldBe "app_"
        config.ignoreMissingTranslationWarnings shouldBe true
        config.index shouldBe "name"
    }
}
