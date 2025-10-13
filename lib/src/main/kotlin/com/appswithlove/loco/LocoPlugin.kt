package com.appswithlove.loco

import org.gradle.api.Plugin
import org.gradle.api.Project

class LocoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val locoExtension = project.extensions.create(
            LocoExtension.NAME,
            LocoExtension::class.java,
        )
        val configListProvider = project.providers.provider { locoExtension.configList }

        project.tasks.register(LocoTask.NAME, LocoTask::class.java) { task ->
            task.configList.set(configListProvider)
        }
    }
}
