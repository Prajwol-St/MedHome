package com.example.medhomeapp.model

data class SpecializationModel(
    val id: String = "",
    val name: String = "",
    val iconUrl: String = "",
    val doctorCount: Int = 0,
    val description: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "iconUrl" to iconUrl,
        "doctorCount" to doctorCount,
        "description" to description
    )
}