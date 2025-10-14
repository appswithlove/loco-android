import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.loco)
}

android {
    namespace = "com.appswithlove.loco.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.appswithlove.loco.sample"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.core.ktx)
    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    debugImplementation(libs.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}

Loco {
    config {
        apiKey = "LBsleEFqO2zZeYaxU_d7pB8v4XBralr1"
        lang = listOf("en", "de")
        defLang = "en"
        fallbackLang = "en"
        resDir = "$projectDir/src/main/res"
        hideComments = true
        orderByAssetId = true
    }
}
