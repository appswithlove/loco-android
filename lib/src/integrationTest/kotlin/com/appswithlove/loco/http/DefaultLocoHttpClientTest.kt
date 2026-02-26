package com.appswithlove.loco.http

import com.appswithlove.loco.plugin.LocoConfig
import com.sun.net.httpserver.HttpServer
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

class DefaultLocoHttpClientTest {

    private lateinit var server: HttpServer
    private var capturedUri: String = ""

    @BeforeEach
    fun setUp() {
        server = HttpServer.create(InetSocketAddress(0), 0)
        server.createContext("/") { exchange ->
            capturedUri = exchange.requestURI.toString()
            val response = "<resources></resources>".toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
        }
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.stop(0)
    }

    private fun config(block: LocoConfig.() -> Unit = {}): LocoConfig = LocoConfig().apply {
        apiKey = "test-key"
        locoBaseUrl = "http://localhost:${server.address.port}"
        block()
    }

    @Test
    fun fetchTranslationWithTagsIncludesFilterParam() {
        val config = config { tags = "android" }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldContain "filter=android"
    }

    @Test
    fun fetchTranslationWithFallbackLangIncludesFallbackParam() {
        val config = config { fallbackLang = "en" }
        DefaultLocoHttpClient().fetchTranslation(config, "de")

        capturedUri shouldContain "fallback=en"
    }

    @Test
    fun fetchTranslationWithOrderByAssetIdIncludesOrderParam() {
        val config = config { orderByAssetId = true }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldContain "order=id"
    }

    @Test
    fun fetchTranslationWithOrderByAssetIdFalseOmitsOrderParam() {
        val config = config { orderByAssetId = false }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldNotContain "order=id"
    }

    @Test
    fun fetchTranslationWithHideCommentsSetsNoCommentsTrue() {
        val config = config { hideComments = true }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldContain "no-comments=true"
    }

    @Test
    fun fetchTranslationWithIndexIncludesIndexParam() {
        val config = config { index = "name" }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldContain "index=name"
    }

    @Test
    fun fetchTranslationWithStatusIncludesStatusParam() {
        val config = config { status = "translated" }
        DefaultLocoHttpClient().fetchTranslation(config, "en")

        capturedUri shouldContain "status=translated"
    }
}
