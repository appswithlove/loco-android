package com.appswithlove.loco

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Task to update from multiple loco files
 */
class LocoMultipleTask extends DefaultTask {
    // Name of the task
    public static String NAME = 'updateLocoMultiple'

    @Internal
    LocoExtension extension

    LocoMultipleTask() {
        project.afterEvaluate {
            extension = project.extensions.LocoMultiple
        }
    }

    @TaskAction
    void doLast() {
        if (extension.configs == null || extension.configs.size() == 0) {
            throw new GradleException("Could not find multiple configs. If using a single config, try to use 'updateLoco' command instead")
        }

        // Iterate on each configuration and generate xml file based on it
        extension.configs.each {
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
