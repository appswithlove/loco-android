package com.appswithlove.loco

import org.gradle.api.Plugin
import org.gradle.api.Project

class LocoPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(LocoExtension.NAME, LocoConfig)
        target.extensions.create(LocoExtension.MULTIPLE_NAME, LocoExtension)
        target.tasks.create(LocoTask.NAME, LocoTask)
        target.tasks.create(LocoMultipleTask.NAME, LocoMultipleTask)
    }
}
