package com.appswithlove.loco

import org.gradle.api.Plugin
import org.gradle.api.Project

class LocoPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(LocoExtension.NAME, LocoExtension, target)
        target.tasks.create(LocoTask.NAME, LocoTask)
    }
}
