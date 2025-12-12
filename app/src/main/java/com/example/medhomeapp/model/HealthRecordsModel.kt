package com.example.medhomeapp.model

data class HealthRecordsModel(
    val id : String = "",
    val userId: String = "",
    val title: String = "",
    val description: String="",
    val fileUrl : String ="",
    val fileName: String ="",
    val timestamp: Long = System.currentTimeMillis()
)
