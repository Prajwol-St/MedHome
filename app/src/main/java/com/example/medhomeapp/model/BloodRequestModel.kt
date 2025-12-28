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
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "patientName" to patientName,
            "bloodGroup" to bloodGroup,
            "hospital" to hospital,
            "location" to location,
            "unitsNeeded" to unitsNeeded,
            "contactNumber" to contactNumber,
            "urgency" to urgency,
            "additionalNotes" to additionalNotes,
            "timestamp" to timestamp,
            "status" to status
        )
    }
    fun isUrgent(): Boolean = urgency == "Urgent"

    fun isActive(): Boolean = status == "active"

    fun isFulfilled(): Boolean = status == "fulfilled"

    fun isCancelled(): Boolean = status == "cancelled"
}
