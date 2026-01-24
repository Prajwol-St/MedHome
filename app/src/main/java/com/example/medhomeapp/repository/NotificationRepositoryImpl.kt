package com.example.medhomeapp.repository

import com.example.medhomeapp.model.*
import com.example.medhomeapp.model.extensions.shouldScheduleReminders
import com.example.medhomeapp.utils.NotificationConstants
import com.google.firebase.database.*
import java.util.UUID

class NotificationRepositoryImpl : NotificationRepository {

    private val database = FirebaseDatabase.getInstance()
    private val medicineRemindersRef = database.getReference("medicineReminders")
    private val notificationPreferencesRef = database.getReference("notificationPreferences")
    private val notificationHistoryRef = database.getReference("notificationHistory")

    // ============ Medicine Reminders ============

    override fun getMedicineReminders(
        userId: String,
        callback: (List<MedicineReminderModel>) -> Unit
    ) {
        medicineRemindersRef.child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reminders = mutableListOf<MedicineReminderModel>()
                    snapshot.children.forEach { child ->
                        child.getValue(MedicineReminderModel::class.java)?.let {
                            reminders.add(it)
                        }
                    }
                    callback(reminders)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun getMedicineReminder(
        userId: String,
        medicineId: String,
        callback: (MedicineReminderModel?) -> Unit
    ) {
        medicineRemindersRef.child(userId).child(medicineId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reminder = snapshot.getValue(MedicineReminderModel::class.java)
                    callback(reminder)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    override fun addMedicineReminder(
        medicineReminder: MedicineReminderModel,
        callback: (Boolean, String) -> Unit
    ) {
        android.util.Log.d("MedicineDebug", "=== REPOSITORY ADD ===")
        android.util.Log.d("MedicineDebug", "Medicine: ${medicineReminder.medicineName}")
        android.util.Log.d("MedicineDebug", "Times: ${medicineReminder.reminderTimes}")
        android.util.Log.d("MedicineDebug", "UserId: ${medicineReminder.userId}")

        val medicineId = medicineReminder.medicineId.ifEmpty {
            medicineRemindersRef.child(medicineReminder.userId).push().key ?: UUID.randomUUID().toString()
        }

        val reminderWithId = medicineReminder.copy(
            medicineId = medicineId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        android.util.Log.d("MedicineDebug", "Saving to path: medicineReminders/${medicineReminder.userId}/$medicineId")

        medicineRemindersRef.child(medicineReminder.userId).child(medicineId)
            .setValue(reminderWithId.toMap())
            .addOnSuccessListener {
                android.util.Log.d("MedicineDebug", "✅ Firebase save SUCCESS")
                callback(true, "Medicine reminder added successfully")
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("MedicineDebug", "❌ Firebase save FAILED: ${exception.message}")
                callback(false, exception.message ?: "Failed to add medicine reminder")
            }
    }

    override fun updateMedicineReminder(
        medicineReminder: MedicineReminderModel,
        callback: (Boolean, String) -> Unit
    ) {
        val updatedReminder = medicineReminder.copy(
            updatedAt = System.currentTimeMillis()
        )

        medicineRemindersRef.child(medicineReminder.userId).child(medicineReminder.medicineId)
            .setValue(updatedReminder.toMap())
            .addOnSuccessListener {
                callback(true, "Medicine reminder updated successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to update medicine reminder")
            }
    }

    override fun deleteMedicineReminder(
        userId: String,
        medicineId: String,
        callback: (Boolean, String) -> Unit
    ) {
        medicineRemindersRef.child(userId).child(medicineId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Medicine reminder deleted successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to delete medicine reminder")
            }
    }

    override fun toggleMedicineReminder(
        userId: String,
        medicineId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        medicineRemindersRef.child(userId).child(medicineId).child("isEnabled")
            .setValue(isEnabled)
            .addOnSuccessListener {
                medicineRemindersRef.child(userId).child(medicineId).child("updatedAt")
                    .setValue(System.currentTimeMillis())

                callback(true, if (isEnabled) "Reminder enabled" else "Reminder disabled")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to toggle reminder")
            }
    }

    // ============ Notification Preferences ============

    override fun getNotificationPreferences(
        userId: String,
        callback: (NotificationPreferencesModel?) -> Unit
    ) {
        notificationPreferencesRef.child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val preferences = snapshot.getValue(NotificationPreferencesModel::class.java)
                        ?: NotificationPreferencesModel(userId = userId)
                    callback(preferences)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(NotificationPreferencesModel(userId = userId))
                }
            })
    }

    override fun updateNotificationPreferences(
        preferences: NotificationPreferencesModel,
        callback: (Boolean, String) -> Unit
    ) {
        val updatedPreferences = preferences.copy(
            updatedAt = System.currentTimeMillis()
        )

        notificationPreferencesRef.child(preferences.userId)
            .setValue(updatedPreferences.toMap())
            .addOnSuccessListener {
                callback(true, "Preferences updated successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to update preferences")
            }
    }

    override fun toggleMedicineRemindersGlobally(
        userId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        notificationPreferencesRef.child(userId).child("medicineRemindersEnabled")
            .setValue(isEnabled)
            .addOnSuccessListener {
                notificationPreferencesRef.child(userId).child("updatedAt")
                    .setValue(System.currentTimeMillis())

                callback(true, "Medicine reminders ${if (isEnabled) "enabled" else "disabled"}")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to toggle medicine reminders")
            }
    }

    override fun toggleAppointmentRemindersGlobally(
        userId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        notificationPreferencesRef.child(userId).child("appointmentRemindersEnabled")
            .setValue(isEnabled)
            .addOnSuccessListener {
                notificationPreferencesRef.child(userId).child("updatedAt")
                    .setValue(System.currentTimeMillis())

                callback(true, "Appointment reminders ${if (isEnabled) "enabled" else "disabled"}")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to toggle appointment reminders")
            }
    }

    // ============ Notification History ============

    override fun getNotificationHistory(
        userId: String,
        callback: (List<NotificationHistoryModel>) -> Unit
    ) {
        notificationHistoryRef.child(userId)
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<NotificationHistoryModel>()
                    snapshot.children.forEach { child ->
                        child.getValue(NotificationHistoryModel::class.java)?.let {
                            notifications.add(it)
                        }
                    }
                    // Sort by timestamp descending (newest first)
                    notifications.sortByDescending { it.timestamp }
                    callback(notifications)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun getUnreadNotificationCount(
        userId: String,
        callback: (Int) -> Unit
    ) {
        notificationHistoryRef.child(userId)
            .orderByChild("isRead")
            .equalTo(false)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(0)
                }
            })
    }

    override fun addNotificationToHistory(
        notification: NotificationHistoryModel,
        callback: (Boolean, String) -> Unit
    ) {
        val notificationId = notification.notificationId.ifEmpty {
            notificationHistoryRef.child(notification.userId).push().key
                ?: UUID.randomUUID().toString()
        }

        val notificationWithId = notification.copy(
            notificationId = notificationId,
            timestamp = System.currentTimeMillis()
        )

        notificationHistoryRef.child(notification.userId).child(notificationId)
            .setValue(notificationWithId.toMap())
            .addOnSuccessListener {
                callback(true, "Notification saved")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to save notification")
            }
    }

    override fun markNotificationAsRead(
        userId: String,
        notificationId: String,
        callback: (Boolean, String) -> Unit
    ) {
        notificationHistoryRef.child(userId).child(notificationId).child("isRead")
            .setValue(true)
            .addOnSuccessListener {
                callback(true, "Notification marked as read")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to mark as read")
            }
    }

    override fun markAllNotificationsAsRead(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        notificationHistoryRef.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updates = mutableMapOf<String, Any>()
                    snapshot.children.forEach { child ->
                        updates["${child.key}/isRead"] = true
                    }

                    if (updates.isEmpty()) {
                        callback(true, "No notifications to mark")
                        return
                    }

                    notificationHistoryRef.child(userId).updateChildren(updates)
                        .addOnSuccessListener {
                            callback(true, "All notifications marked as read")
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message ?: "Failed to mark all as read")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }

    override fun deleteNotificationFromHistory(
        userId: String,
        notificationId: String,
        callback: (Boolean, String) -> Unit
    ) {
        notificationHistoryRef.child(userId).child(notificationId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Notification deleted")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to delete notification")
            }
    }

    // ============ Appointment Notification Scheduling ============

    override fun scheduleAppointmentReminders(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Check if appointment reminders are enabled
        areAppointmentRemindersEnabled(appointment.patientId) { enabled ->
            if (!enabled) {
                callback(false, "Appointment reminders are disabled")
                return@areAppointmentRemindersEnabled
            }

            // Check if appointment should have reminders
            if (!appointment.shouldScheduleReminders()) {
                callback(false, "Appointment is not eligible for reminders")
                return@areAppointmentRemindersEnabled
            }

            callback(true, "Appointment reminders scheduled")
        }
    }

    override fun cancelAppointmentReminders(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    ) {
        callback(true, "Appointment reminders cancelled")
    }

    override fun sendBookingConfirmation(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ) {
        val notification = NotificationHistoryModel(
            userId = appointment.patientId,
            type = NotificationConstants.BOOKING_CONFIRMATION,
            title = "Appointment Confirmed",
            message = "Your appointment with Dr. ${appointment.doctorName} on ${appointment.date} at ${appointment.time} has been confirmed.",
            relatedId = appointment.appointmentId,
            relatedType = "appointment"
        )

        addNotificationToHistory(notification) { success, message ->
            if (success) {
                callback(true, "Booking confirmation sent")
            } else {
                callback(false, message)
            }
        }
    }

    override fun areAppointmentRemindersEnabled(
        userId: String,
        callback: (Boolean) -> Unit
    ) {
        notificationPreferencesRef.child(userId).child("appointmentRemindersEnabled")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val enabled = snapshot.getValue(Boolean::class.java) ?: true
                    callback(enabled)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(true) // Default to enabled
                }
            })
    }

    override fun areMedicineRemindersEnabled(
        userId: String,
        callback: (Boolean) -> Unit
    ) {
        notificationPreferencesRef.child(userId).child("medicineRemindersEnabled")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val enabled = snapshot.getValue(Boolean::class.java) ?: true
                    callback(enabled)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(true) // Default to enabled
                }
            })
    }
}