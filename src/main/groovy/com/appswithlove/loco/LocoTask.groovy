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
        for (String lang in project.Loco.lang) {
            def baseUrl = new URL("${project.Loco.locoBaseUrl}/${lang}.xml")
            HttpURLConnection connection = (HttpURLConnection) baseUrl.openConnection()
            connection.addRequestProperty("Authorization", "Loco ${project.Loco.apiKey}")
            connection.with {
                doOutput = true
                requestMethod = 'GET'
                new File("${project.Loco.resDir}/values-$lang/strings.xml").text = content.text.replaceAll(/\$.*\$/, "%s")

                if (lang == project.Loco.defLang) {
                    new File("${project.Loco.resDir}/values/strings.xml").text = new File("${project.Loco.resDir}/values-$lang/strings.xml").text
                }
            }
        }
        //}
    }
}
