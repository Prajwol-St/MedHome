package com.example.medhomeapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class DoctorModel(
    val id: String = "",
    val role: String = "doctor",   // Important: default role is "doctor"
    val name: String = "",
    val email: String = "",
    val contact: String = "",
    val gender: String = "",
    val specialization: String = "", // add doctor-specific field
    val dateOfBirth: String = "",
    val emailVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val address: String = ""
) : Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role" to role,
            "name" to name,
            "email" to email,
            "contact" to contact,
            "gender" to gender,
            "specialization" to specialization,
            "dateOfBirth" to dateOfBirth,
            "emailVerified" to emailVerified,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "address" to address
        )
    }
}
