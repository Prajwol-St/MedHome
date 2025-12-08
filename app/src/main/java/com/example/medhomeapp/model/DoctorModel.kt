package com.example.medhomeapp.model

data class DoctorModel(
    val id: Int = 0,
    val role: String,
    val userId: Int = 0,
    val name: String = "",
    val specialization: String = "",
    val type: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role" to role,
            "userId" to userId,
            "name" to name,
            "specialization" to specialization,
            "type" to type
        )
    }
}
