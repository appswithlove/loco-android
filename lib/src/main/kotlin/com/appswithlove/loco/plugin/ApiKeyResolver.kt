package com.appswithlove.loco.plugin

import org.gradle.api.Project
import java.io.File
import java.util.Properties

internal fun resolveApiKey(project: Project): String? = resolveApiKey(
    findProperty = { project.findProperty("locoApiKey") as? String },
    localPropertiesFile = { project.rootProject.file("local.properties") },
)

internal fun resolveApiKey(
    findProperty: () -> String?,
    localPropertiesFile: () -> File,
    getEnv: (String) -> String? = System::getenv,
): String? {
    findProperty()?.takeIf { it.isNotBlank() }?.let { return it }

    val localProps = localPropertiesFile()
    if (localProps.exists()) {
        val props = Properties()
        localProps.inputStream().use { props.load(it) }
        props.getProperty("locoApiKey")?.takeIf { it.isNotBlank() }?.let { return it }
    }

    return getEnv("LOCO_API_KEY")?.takeIf { it.isNotBlank() }
}
