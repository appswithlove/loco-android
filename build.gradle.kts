buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
        mavenCentral()
    }
    dependencies {
        classpath(libs.dokka.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.gradle.publish) apply false
    alias(libs.plugins.coveralls)
    alias(libs.plugins.loco) apply false
    id("signing")
}
