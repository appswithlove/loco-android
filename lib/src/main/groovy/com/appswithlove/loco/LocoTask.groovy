package com.appswithlove.loco

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Task to update from a single loco file
 */
class LocoTask extends DefaultTask {
    // Name of the task
    public static String NAME = 'updateLoco'

    @Input
    List<String> configList = []

    @TaskAction
    void doLast() {
        if (configList.isEmpty()) {
            throw new GradleException("Could not find any Loco Configs.")
        }

        // Iterate on each configuration and generate xml file based on it
        configList.each {
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
