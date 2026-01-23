package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.model.NotificationPreferencesModel

interface NotificationRepository {
    fun getMedicineReminders(
        userId: String,
        callback: (List<MedicineReminderModel>) -> Unit
    )
    fun getMedicineReminder(
        userId: String,
        medicineId: String,
        callback: (MedicineReminderModel?) -> Unit
    )
    fun addMedicineReminder(
        medicineReminder: MedicineReminderModel,
        callback: (Boolean, String) -> Unit
    )
    fun updateMedicineReminder(
        medicineReminder: MedicineReminderModel,
        callback: (Boolean, String) -> Unit
    )
    fun deleteMedicineReminder(
        userId: String,
        medicineId: String,
        callback: (Boolean, String) -> Unit
    )
    fun toggleMedicineReminder(
        userId: String,
        medicineId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    )
    fun getNotificationPreferences(
        userId: String,
        callback: (NotificationPreferencesModel?) -> Unit
    )
    fun updateNotificationPreferences(
        preferences: NotificationPreferencesModel,
        callback: (Boolean, String) -> Unit
    )
    fun toggleMedicineRemindersGlobally(
        userId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    )

        fun toggleAppointmentRemindersGlobally(
        userId: String,
        isEnabled: Boolean,
        callback: (Boolean, String) -> Unit
    )

    fun getNotificationHistory(
        userId: String,
        callback: (List<NotificationHistoryModel>) -> Unit
    )

    fun getUnreadNotificationCount(
        userId: String,
        callback: (Int) -> Unit
    )

    fun addNotificationToHistory(
        notification: NotificationHistoryModel,
        callback: (Boolean, String) -> Unit
    )

    fun markNotificationAsRead(
        userId: String,
        notificationId: String,
        callback: (Boolean, String) -> Unit
    )

    fun markAllNotificationsAsRead(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    fun deleteNotificationFromHistory(
        userId: String,
        notificationId: String,
        callback: (Boolean, String) -> Unit
    )

    fun scheduleAppointmentReminders(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun cancelAppointmentReminders(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    )

    fun sendBookingConfirmation(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun areAppointmentRemindersEnabled(
        userId: String,
        callback: (Boolean) -> Unit
    )

    fun areMedicineRemindersEnabled(
        userId: String,
        callback: (Boolean) -> Unit
    )
}