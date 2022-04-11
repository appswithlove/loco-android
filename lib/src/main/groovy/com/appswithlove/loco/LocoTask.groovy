package com.appswithlove.loco

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Task to update from a single loco file
 */
class LocoTask extends DefaultTask {
    // Name of the task
    public static String NAME = 'updateLoco'

    @Internal
    LocoConfig extension

    LocoTask() {
        project.afterEvaluate {
            extension = project.extensions."${LocoExtension.NAME}"
        }
    }

    @TaskAction
    void doLast() {
        TaskUtils.generate(project.Loco)
        ok()
    }

    private static void ok() {
        println()
        println("--------------------------------------")
        println("Loco text strings updated successfully!")
        println("--------------------------------------")
    }
}
