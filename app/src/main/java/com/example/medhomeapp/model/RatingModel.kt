package com.example.medhomeapp.model

data class RatingModel(
    val ratingId: String = "",
    val appointmentId: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val doctorId: String = "",
    val rating: Float = 0f, // 1.0 to 5.0
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false,
    val editedAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "ratingId" to ratingId,
        "appointmentId" to appointmentId,
        "patientId" to patientId,
        "patientName" to patientName,
        "doctorId" to doctorId,
        "rating" to rating,
        "review" to review,
        "createdAt" to createdAt,
        "isEdited" to isEdited,
        "editedAt" to editedAt
    )
}