package com.appswithlove.loco.plugin

import com.appswithlove.loco.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class LocoPushTask : DefaultTask() {

    companion object {
        const val NAME = "locoPush"
    }

    @get:Input
    abstract val configList: ListProperty<LocoConfig>

    @TaskAction
    fun doLast() {
        if (configList.get().isEmpty()) {
            throw GradleException("Could not find any Loco Configs.")
        }

        configList.get().forEach { config ->
            TaskUtils.push(config)
        }

        println()
        println("--------------------------------------")
        println("Loco text strings pushed successfully!")
        println("--------------------------------------")
    }
}
