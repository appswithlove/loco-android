package com.appswithlove.loco.plugin

import com.appswithlove.loco.extensions.LocoExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LocoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val locoExtension = project.extensions.create(
            LocoExtension.Companion.NAME,
            LocoExtension::class.java,
        )
        val configListProvider = project.providers.provider { locoExtension.configList }

        project.tasks.register(LocoTask.NAME, LocoTask::class.java) { task ->
            task.configList.set(configListProvider)
        }
    }
}
