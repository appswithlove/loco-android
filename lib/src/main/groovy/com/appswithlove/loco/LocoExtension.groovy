package com.appswithlove.loco

import org.gradle.api.Project

class LocoExtension {
    public static final String NAME = 'Loco'

    Project project
    List<LocoConfig> configList = []

    LocoExtension(Project project) {
        this.project = project
    }

    void config(Closure closure) {
        LocoConfig config = new LocoConfig()
        project.configure(config, closure)
        configList.add(config)
    }
}
