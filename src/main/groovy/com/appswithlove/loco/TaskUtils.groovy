package com.appswithlove.loco

/**
 * Helper to generate xml file from a loco config
 */
class TaskUtils {
    static void generate(LocoConfig locoConfig) {
        // Determine whether it should show comments or not
        def parameter = "?no-comments=${locoConfig.hideComments}"

        // Add tags filter
        if (locoConfig.tags != null) {
            parameter = parameter + "&filter=${locoConfig.tags}"
        }

        // If a fallback language was specified, include it as a parameter
        if (locoConfig.fallbackLang != null) {
            parameter = parameter + "&fallback=${locoConfig.fallbackLang}"
        }

        // Determine whether assets should be ordered alphabetically by Asset ID, include parameter if necessary
        if (locoConfig.orderByAssetId) {
            parameter = parameter + "&order=id"
        }

        for (String lang in locoConfig.lang) {
            def baseUrl = new URL("${locoConfig.locoBaseUrl}/${lang}.xml${parameter}")
            HttpURLConnection connection = (HttpURLConnection) baseUrl.openConnection()
            connection.addRequestProperty("Authorization", "Loco ${locoConfig.apiKey}")
            connection.with {
                doOutput = true
                requestMethod = 'GET'

                def text = new String(content.text.getBytes("UTF-8"), "UTF-8")

                if (locoConfig.placeholderPattern != null) {
                    text = text.replaceAll(locoConfig.placeholderPattern, "%s")
                }

                def appendix = ""
                if (lang != locoConfig.defLang) {
                    // Certain languages have multiple regions (e.g., Spanish (Spain) and Spanish (Mexico)),
                    // and their folder titles have an additional "r" in them.
                    if (lang.contains("-")) {
                        lang = lang.replace("-", "-r")
                    }

                    appendix = "-$lang"
                }

                def directory = new File("${locoConfig.resDir}/values$appendix/")
                if (!directory.exists()) {
                    directory.mkdir()
                }

                def file = new File(directory.absolutePath + "/" + locoConfig.fileName + ".xml")
                file.write(text)
            }
        }
    }
}
