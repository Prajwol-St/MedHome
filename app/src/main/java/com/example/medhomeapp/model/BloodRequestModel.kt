package com.example.medhomeapp.model

data class BloodRequestModel(
    val id: String = "",
    val patientName: String = "",
    val bloodGroup: String = "",
    val hospital: String = "",
    val location: String = "",
    val unitsNeeded: String = "",
    val contactNumber: String = "",
    val urgency: String ="",
    val additionalNotes: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "active"
)
