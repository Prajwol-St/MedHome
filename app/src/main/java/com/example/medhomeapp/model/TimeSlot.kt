package com.example.medhomeapp.model

data class TimeSlot(
    val id: String = "",
    val doctorId: String = "",  // Add doctorId field
    val day: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isAvailable: Boolean = true
)