package com.example.medhomeapp.model

import com.google.type.Date

data class AppointmentModel(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val date: String = "",
    val time: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "patientId" to patientId,
            "doctorId" to doctorId,
            "date" to date,
            "time" to time
        )
}
}

