package com.appswithlove.loco.plugin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ApiKeyResolverTest {

    @TempDir
    lateinit var tempDir: File

    private fun resolve(
        property: String? = null,
        localPropsContent: String? = null,
        envValue: String? = null,
    ): String? {
        val localPropsFile = File(tempDir, "local.properties")
        if (localPropsContent != null) localPropsFile.writeText(localPropsContent)
        return resolveApiKey(
            findProperty = { property },
            localPropertiesFile = { localPropsFile },
            getEnv = { envValue },
        )
    }

    @Test
    fun resolveApiKey_withNoSources_returnsNull() {
        resolve() shouldBe null
    }

    @Test
    fun resolveApiKey_withGradleProperty_returnsValue() {
        resolve(property = "gradle-key") shouldBe "gradle-key"
    }

    @Test
    fun resolveApiKey_withLocalProperties_returnsValue() {
        resolve(localPropsContent = "locoApiKey=my-key\n") shouldBe "my-key"
    }

    @Test
    fun resolveApiKey_withMissingLocalPropertiesFile_doesNotThrow() {
        // No localPropsContent written — file absent
        resolve() shouldBe null
    }

    @Test
    fun resolveApiKey_withEnvVar_returnsValue() {
        resolve(envValue = "env-key") shouldBe "env-key"
    }

    @Test
    fun resolveApiKey_withBlankGradleProperty_fallsThrough() {
        resolve(
            property = "   ",
            localPropsContent = "locoApiKey=fallback-key\n",
        ) shouldBe "fallback-key"
    }

    @Test
    fun resolveApiKey_gradlePropertyTakesPriorityOverLocalProperties() {
        resolve(
            property = "gradle-key",
            localPropsContent = "locoApiKey=local-key\n",
        ) shouldBe "gradle-key"
    }

    @Test
    fun resolveApiKey_localPropertiesTakesPriorityOverEnvVar() {
        resolve(
            localPropsContent = "locoApiKey=local-key\n",
            envValue = "env-key",
        ) shouldBe "local-key"
    }

    @Test
    fun resolveApiKey_withBlankEnvVar_returnsNull() {
        resolve(envValue = "   ") shouldBe null
    }
}
