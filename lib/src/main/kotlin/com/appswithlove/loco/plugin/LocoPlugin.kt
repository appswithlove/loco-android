package com.appswithlove.loco.plugin

import com.appswithlove.loco.extensions.LocoExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LocoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val locoExtension = project.extensions.create(
            LocoExtension.NAME,
            LocoExtension::class.java,
        )
        val configListProvider = project.providers.provider { locoExtension.configList }

        project.tasks.register(LocoFetchTask.NAME, LocoFetchTask::class.java) { task ->
            task.configList.set(configListProvider)
        }

        project.tasks.register("updateLoco", LocoFetchTask::class.java) { task ->
            task.configList.set(configListProvider)
            task.description = "Deprecated: use locoFetch instead."
        }

        project.tasks.register(LocoPushTask.NAME, LocoPushTask::class.java) { task ->
            task.configList.set(configListProvider)
        }
    }
}
