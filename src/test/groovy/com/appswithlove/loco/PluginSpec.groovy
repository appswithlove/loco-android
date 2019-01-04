package com.appswithlove.loco

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpec extends Specification {
    static final String PLUGIN_ID = 'com.appswithlove.loco'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir

    void setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
    }

    void apply() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: PLUGIN_ID

        expect:
        project.tasks."${LocoTask.NAME}" instanceof LocoTask
    }

   /* void executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${LocoExtension.NAME}".with {

        }
        project.evaluate()

        when:
        project.tasks."${LocoTask.NAME}".execute()

        then:
        notThrown(Exception)
        // TODO Add assertions here if required
    }*/
}
