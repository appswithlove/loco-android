import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.gradle.kotlin.dsl.signing
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.gradle.publish)
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    signing
}

dependencies {
    implementation(gradleApi())
    testImplementation(gradleTestKit())
    testRuntimeOnly(libs.cglib.nodep)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradle)
    implementation(libs.gradle.api)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization.json)
}

tasks.withType<Test>().configureEach {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    jvmToolchain(11)
}

mavenPublishing {
    configure(
        GradlePlugin(
            javadocJar = JavadocJar.None(),
            sourcesJar = true,
        )
    )
}

val pluginId: String = findProperty("GROUP") as String? ?: ""
val pluginName: String = findProperty("POM_NAME") as String? ?: ""
val pluginDescription: String = findProperty("POM_DESCRIPTION") as String? ?: ""
val pluginVersion: String = findProperty("VERSION_NAME") as String? ?: ""
val pluginWebsite: String = findProperty("POM_URL") as String? ?: ""
val pluginVcsUrl: String = findProperty("POM_SCM_URL") as String? ?: ""

gradlePlugin {
    website.set(pluginWebsite)
    vcsUrl.set(pluginVcsUrl)

    plugins {
        create("loco") {
            id = pluginId
            displayName = pluginName
            description = pluginDescription
            version = pluginVersion
            tags = listOf("loco", "localization", "android")
            implementationClass = "com.appswithlove.loco.plugin.LocoPlugin"
        }
    }
}

signing {
    val signingInMemoryKey = project.findProperty("signingInMemoryKey") as String?
    val signingInMemoryPassword = project.findProperty("signingInMemoryPassword") as String?
    if (signingInMemoryKey != null && signingInMemoryPassword != null) {
        useInMemoryPgpKeys(signingInMemoryKey, signingInMemoryPassword)
        sign(publishing.publications)
    }
}
