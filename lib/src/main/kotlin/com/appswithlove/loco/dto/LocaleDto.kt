package com.appswithlove.loco.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocaleDto(
    @SerialName("code") val code: String
)
