package com.appswithlove.loco.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PushResultDto(
    @SerialName("message") val message: String = "",
    @SerialName("locales") val locales: List<PushLocaleDto> = emptyList(),
)

@Serializable
internal data class PushLocaleDto(
    @SerialName("progress") val progress: PushProgressDto = PushProgressDto(),
)

@Serializable
internal data class PushProgressDto(
    @SerialName("translated") val translated: Int = 0,
    @SerialName("untranslated") val untranslated: Int = 0,
)
