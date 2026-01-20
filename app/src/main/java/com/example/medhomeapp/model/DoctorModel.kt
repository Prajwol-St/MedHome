package com.example.medhomeapp.model

data class DoctorModel(
    val id: String = "",
    val role: String = "doctor",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val specialization: String = "",
    val subSpecialization: String = "",
    val type: String = "", // Specialist, General Practitioner
    val experience: Int = 0, // years
    val qualifications: String = "",
    val about: String = "",
    val consultationFee: Double = 0.0,
    val clinicName: String = "",
    val clinicAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val averageRating: Float = 0f,
    val totalRatings: Int = 0,
    val totalReviews: Int = 0,
    val defaultSlotDuration: Int = 30, // minutes
    val isVerified: Boolean = false,
    val isAvailableToday: Boolean = false,
    val languages: String = "", // "English, Nepali, Hindi"
    val createdAt: String = System.currentTimeMillis().toString()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "role" to role,
        "userId" to userId,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "profileImage" to profileImage,
        "specialization" to specialization,
        "subSpecialization" to subSpecialization,
        "type" to type,
        "experience" to experience,
        "qualifications" to qualifications,
        "about" to about,
        "consultationFee" to consultationFee,
        "clinicName" to clinicName,
        "clinicAddress" to clinicAddress,
        "latitude" to latitude,
        "longitude" to longitude,
        "averageRating" to averageRating,
        "totalRatings" to totalRatings,
        "totalReviews" to totalReviews,
        "defaultSlotDuration" to defaultSlotDuration,
        "isVerified" to isVerified,
        "isAvailableToday" to isAvailableToday,
        "languages" to languages,
        "createdAt" to createdAt
    )
}