package com.appswithlove.loco

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Task to update from a single loco file
 */
class LocoTask extends DefaultTask {
    // Name of the task
    public static String NAME = 'updateLoco'

    @Internal
    LocoExtension extension

    LocoTask() {
        project.afterEvaluate {
            extension = project.extensions."${LocoExtension.NAME}"
        }
    }

    @TaskAction
    void doLast() {
        if (extension.configList.size() == 0) {
            throw new GradleException("Could not find any Loco Configs.")
        }

        // Iterate on each configuration and generate xml file based on it
        extension.configList.each {
            TaskUtils.generate(it)
        }

        ok()
    }

    private static void ok() {
        println()
        println("--------------------------------------")
        println("Loco text strings updated successfully!")
        println("--------------------------------------")
    }
}
