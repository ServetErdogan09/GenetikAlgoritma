package com.serveterdogan.genetikalgoritmamobil.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Availability(
    val id: Int? = null,
    @SerialName("teacher_id")
    val teacherId: Int,
    val day: String,
    @SerialName("start_hour")
    val startHour: Int,
    @SerialName("end_hour")
    val endHour: Int,
    @SerialName("is_available")
    val isAvailable: Boolean
)