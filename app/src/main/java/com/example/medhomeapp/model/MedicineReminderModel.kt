package com.example.medhomeapp.model

data class MedicineReminderModel(
    val medicineId: String = "",
    val userId: String = "",
    val medicineName: String = "",
    val dosage: String = "",
    val reminderTimes: List<String> = emptyList(),
    val frequency: String = "daily",
    val isEnabled: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val instructions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "medicineId" to medicineId,
        "userId" to userId,
        "medicineName" to medicineName,
        "dosage" to dosage,
        "reminderTimes" to reminderTimes,
        "frequency" to frequency,
        "isEnabled" to isEnabled,
        "startDate" to startDate,
        "endDate" to endDate,
        "instructions" to instructions,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

