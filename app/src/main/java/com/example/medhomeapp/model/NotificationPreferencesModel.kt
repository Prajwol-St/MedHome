package com.example.medhomeapp.model

data class NotificationPreferencesModel(
    val userId: String = "",
    val medicineRemindersEnabled: Boolean = true,
    val appointmentRemindersEnabled: Boolean = true,
    val bookingConfirmationsEnabled: Boolean = true,
    val reminderSound: Boolean = true,
    val vibration: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "medicineRemindersEnabled" to medicineRemindersEnabled,
        "appointmentRemindersEnabled" to appointmentRemindersEnabled,
        "bookingConfirmationsEnabled" to bookingConfirmationsEnabled,
        "reminderSound" to reminderSound,
        "vibration" to vibration,
        "updatedAt" to updatedAt
    )
}

