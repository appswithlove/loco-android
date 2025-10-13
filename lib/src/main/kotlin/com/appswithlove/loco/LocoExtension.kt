package com.appswithlove.loco

import org.gradle.api.Action
import org.gradle.api.Project

open class LocoExtension(private val project: Project) {

    companion object {
        const val NAME = "Loco"
    }

    val configList: MutableList<LocoConfig> = mutableListOf()

    fun config(action: Action<LocoConfig>) {
        val config = LocoConfig()
        project.configure(listOf(config), action)
        configList.add(config)
    }
}
