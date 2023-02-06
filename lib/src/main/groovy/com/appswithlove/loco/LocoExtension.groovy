package com.appswithlove.loco

import org.gradle.api.Action
import org.gradle.api.Project

class LocoExtension {
    public static final String NAME = 'Loco'

    Project project
    List<LocoConfig> configList = []

    LocoExtension(Project project) {
        this.project = project
    }

    void config(Action<LocoConfig> action) {
        LocoConfig config = new LocoConfig()
        project.configure([config], action)
        configList.add(config)
    }

}
