package com.serveterdogan.genetikalgoritmamobil.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClassRoom(
    val id : Int,
    val name : String,
    val capacity : Int,
    val type : String // 'Laboratuvar', 'Amfi', 'Sınıf'
)
