package com.appswithlove.loco.http

import com.appswithlove.loco.plugin.LocoConfig
import com.sun.net.httpserver.HttpServer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

class DefaultLocoHttpClientTest {

    private lateinit var server: HttpServer
    private var capturedUri: String = ""
    private var capturedMethod: String = ""
    private var capturedBody: String = ""
    private var serverResponse: String = "<resources></resources>"

    private fun importBaseUrl() = "http://localhost:${server.address.port}"

    @BeforeEach
    fun setUp() {
        server = HttpServer.create(InetSocketAddress(0), 0)
        server.createContext("/") { exchange ->
            capturedUri = exchange.requestURI.toString()
            capturedMethod = exchange.requestMethod
            capturedBody = exchange.requestBody.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val response = serverResponse.toByteArray()
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

    @Test
    fun pushTranslationSendsPostRequest() {
        serverResponse =
            """{"message":"1 assets imported","locales":[{"progress":{"translated":1,"untranslated":0}}]}"""
        DefaultLocoHttpClient()
            .pushTranslation("key", "en", "<resources/>", importBaseUrl())

        capturedMethod shouldBe "POST"
    }

    @Test
    fun pushTranslationIncludesLocaleParam() {
        serverResponse =
            """{"message":"Nothing updated","locales":[{"progress":{"translated":0,"untranslated":0}}]}"""
        DefaultLocoHttpClient()
            .pushTranslation("key", "de", "<resources/>", importBaseUrl())

        capturedUri shouldContain "locale=de"
    }

    @Test
    fun pushTranslationIncludesDeleteAbsentParam() {
        serverResponse =
            """{"message":"Nothing updated","locales":[{"progress":{"translated":0,"untranslated":0}}]}"""
        DefaultLocoHttpClient()
            .pushTranslation("key", "en", "<resources/>", importBaseUrl())

        capturedUri shouldContain "delete-absent=0"
    }

    @Test
    fun pushTranslationDoesNotIncludeIgnoreExistingParam() {
        serverResponse =
            """{"message":"Nothing updated","locales":[{"progress":{"translated":0,"untranslated":0}}]}"""
        DefaultLocoHttpClient()
            .pushTranslation("key", "en", "<resources/>", importBaseUrl())

        capturedUri shouldNotContain "ignore-existing"
    }

    @Test
    fun pushTranslationSendsXmlBody() {
        serverResponse =
            """{"message":"1 assets imported","locales":[{"progress":{"translated":1,"untranslated":0}}]}"""
        val xml = "<resources><string name=\"hello\">Hello</string></resources>"
        DefaultLocoHttpClient()
            .pushTranslation("key", "en", xml, importBaseUrl())

        capturedBody shouldBe xml
    }
}
