package com.example.medhomeapp.model

data class AppointmentModel(
    val appointmentId: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val patientPhone: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialization: String = "",
    val date: String = "",
    val time: String = "",
    val duration: Int = 30,
    val status: String = "pending",
    val appointmentType: String = "in_person",
    val consultationFee: Double = 0.0,
    val patientNotes: String = "",
    val doctorNotes: String = "",
    val cancellationReason: String = "",
    val cancelledBy: String = "",
    val cancelledAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "appointmentId" to appointmentId,
        "patientId" to patientId,
        "patientName" to patientName,
        "patientPhone" to patientPhone,
        "doctorId" to doctorId,
        "doctorName" to doctorName,
        "specialization" to specialization,
        "date" to date,
        "time" to time,
        "duration" to duration,
        "status" to status,
        "appointmentType" to appointmentType,
        "consultationFee" to consultationFee,
        "patientNotes" to patientNotes,
        "doctorNotes" to doctorNotes,
        "cancellationReason" to cancellationReason,
        "cancelledBy" to cancelledBy,
        "cancelledAt" to cancelledAt,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "completedAt" to completedAt
    )
}