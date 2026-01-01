package com.example.medhomeapp.model

data class AppointmentModel(
    val appointmentId: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val patientName: String = "",
    val date: String = "",
    val time: String = "",
    val reason: String = ""     // scheduled / cancelled / completed
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "patientName" to patientName,
            "date" to date,
            "time" to time
        )
    }
}
