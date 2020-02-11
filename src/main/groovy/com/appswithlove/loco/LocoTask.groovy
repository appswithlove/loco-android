package com.appswithlove.loco

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class LocoTask extends DefaultTask {
    // Name of the task
    public static String NAME = 'updateLoco'
    LocoExtension extension

    LocoTask() {
        project.afterEvaluate {
            extension = project.extensions."${LocoExtension.NAME}"
        }
    }

    @TaskAction
    void doLast() {

        //Determine wheter it should show comments or not
        def parameter = "?no-comments=${project.Loco.hideComments}"

        // Add tags filter
        if (project.Loco.tags != null) {
            parameter = parameter + "&filter=${project.Loco.tags}"
        }

        for (String lang in project.Loco.lang) {
            def baseUrl = new URL("${project.Loco.locoBaseUrl}/${lang}.xml${parameter}")
            HttpURLConnection connection = (HttpURLConnection) baseUrl.openConnection()
            connection.addRequestProperty("Authorization", "Loco ${project.Loco.apiKey}")
            connection.with {
                doOutput = true
                requestMethod = 'GET'

                def text = content.text
                text = new String(text.getBytes("UTF-8"), "UTF-8")

                if (project.Loco.placeholderPattern != null) {
                    text = text.replaceAll(project.Loco.placeholderPattern, "%s")
                }

                if (lang == project.Loco.defLang) {
                    def internalFile = new File("${project.Loco.resDir}/values/strings.xml")
                    internalFile.write(text)
                } else {
                    def file = new File("${project.Loco.resDir}/values-$lang/strings.xml")
                    file.write(text)
                }
            }
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
