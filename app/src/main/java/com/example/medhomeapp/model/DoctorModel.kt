package com.example.medhomeapp.model

data class DoctorModel(
    val id: Int = 0,
    val role: String,
    val userId: Int = 0,
    val name: String = "",
    val specialization: String = "",
    val type: String = "",
){
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "role" to role,
        "userId" to userId,
        "name" to name,
        "specialization" to specialization,
        "type" to type
    )
}


data class DoctorAvailability(
    val doctorId: String = "",   // doctor.id.toString()
    val date: String = "",
    val timeSlots: List<String> = emptyList()
)


data class DoctorLeave(
    val doctorId: String = "",
    val date: String = ""
)

