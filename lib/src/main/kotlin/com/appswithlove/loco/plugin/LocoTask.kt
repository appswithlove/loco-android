package com.appswithlove.loco.plugin

import com.appswithlove.loco.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Task to update from a single loco file
 */
abstract class LocoTask : DefaultTask() {

    companion object {
        const val NAME = "updateLoco"
    }

    @get:Input
    abstract val configList: ListProperty<LocoConfig>

    @TaskAction
    fun doLast() {
        if (configList.get().isEmpty()) {
            throw GradleException("Could not find any Loco Configs.")
        }

        // Iterate over each configuration and generate XML file based on it
        configList.get().forEach { config ->
            TaskUtils.generate(config)
        }

        ok()
    }

    private fun ok() {
        println()
        println("--------------------------------------")
        println("Loco text strings updated successfully!")
        println("--------------------------------------")
    }
}
