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


    data class DoctorAvailability(
        val doctorId: String = "",
        val date: String = "", // yyyy-MM-dd
        val timeSlots: List<String> = emptyList() // e.g. ["10:00", "11:00"]
    )

    data class DoctorLeave(
        val doctorId: String = "",
        val date: String = "" // full day leave
    )

