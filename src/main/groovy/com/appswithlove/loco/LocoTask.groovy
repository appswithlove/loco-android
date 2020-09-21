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

        // Determine whether it should show comments or not
        def parameter = "?no-comments=${project.Loco.hideComments}"

        // Add tags filter
        if (project.Loco.tags != null) {
            parameter = parameter + "&filter=${project.Loco.tags}"
        }

        // If a fallback language was specified, include it as a parameter
        if (project.Loco.fallbackLang != null) {
            parameter = parameter + "&fallback=${project.Loco.fallbackLang}"
        }

        // Determine whether assets should be ordered alphabetically by Asset ID, include parameter if necessary
        if (project.Loco.orderByAssetId) {
            parameter = parameter + "&order=id"
        }

        for (String lang in project.Loco.lang) {
            def baseUrl = new URL("${project.Loco.locoBaseUrl}/${lang}.xml${parameter}")
            HttpURLConnection connection = (HttpURLConnection) baseUrl.openConnection()
            connection.addRequestProperty("Authorization", "Loco ${project.Loco.apiKey}")
            connection.with {
                doOutput = true
                requestMethod = 'GET'

                def text = new String(content.text.getBytes("UTF-8"), "UTF-8")

                if (project.Loco.placeholderPattern != null) {
                    text = text.replaceAll(project.Loco.placeholderPattern, "%s")
                }

                def appendix = ""
                if (lang != project.Loco.defLang) {
                    // Certain languages have multiple regions (e.g., Spanish (Spain) and Spanish (Mexico)),
                    // and their folder titles have an additional "r" in them.
                    if (lang.contains("-")) {
                        lang = lang.replace("-", "-r")
                    }

                    appendix = "-$lang"
                }

                def directory = new File("${project.Loco.resDir}/values$appendix/")
                if (!directory.exists()) {
                    directory.mkdir()
                }

                def file = new File(directory.absolutePath + "/strings.xml")
                file.write(text)
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
