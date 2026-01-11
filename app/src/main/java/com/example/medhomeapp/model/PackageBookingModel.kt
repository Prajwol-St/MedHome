package com.example.medhomeapp.model

data class PackageBookingModel(
    val id: String = "",
    val packageId: String = "",
    val packageName: String = "",
    val packagePrice: Double = 0.0,
    val packageDuration: String = "",
    val packageImageUrl: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val patientEmail: String = "",
    val patientContact: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val bookedAt: String = "",
    val expiresAt: String = "",
    val status: String = "active"
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "packageId" to packageId,
            "packageName" to packageName,
            "packagePrice" to packagePrice,
            "packageDuration" to packageDuration,
            "packageImageUrl" to packageImageUrl,
            "patientId" to patientId,
            "patientName" to patientName,
            "patientEmail" to patientEmail,
            "patientContact" to patientContact,
            "doctorId" to doctorId,
            "doctorName" to doctorName,
            "bookedAt" to bookedAt,
            "expiresAt" to expiresAt,
            "status" to status
        )
    }
}