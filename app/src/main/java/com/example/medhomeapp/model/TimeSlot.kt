package com.example.medhomeapp.model

data class TimeSlot(
    val id: String = "",
    val doctorId: String = "",
    val date: String = "",
    val day: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val duration: Int = 30,
    val isAvailable: Boolean = true,
    val isBooked: Boolean = false,
    val appointmentId: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "doctorId" to doctorId,
        "date" to date,
        "day" to day,
        "startTime" to startTime,
        "endTime" to endTime,
        "duration" to duration,
        "isAvailable" to isAvailable,
        "isBooked" to isBooked,
        "appointmentId" to appointmentId
    )
}