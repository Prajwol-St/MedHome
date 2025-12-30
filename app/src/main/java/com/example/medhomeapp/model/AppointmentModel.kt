package com.example.medhomeapp.model

data class AppointmentModel(
    val id: String = "",
    val userId: String = "",
    val doctorId: String = "",
    val date: String = "",
    val time: String = "",
    val appointmentId: String,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "doctorId" to doctorId,
            "date" to date,
            "time" to time
        )
}
}

