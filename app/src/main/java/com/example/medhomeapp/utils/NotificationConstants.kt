package com.example.medhomeapp.utils

import com.example.medhomeapp.model.AppointmentModel
import java.util.concurrent.TimeUnit

object NotificationConstants {

    // ============ Notification Types ============
    const val APPOINTMENT_24H = "appointment_24h"
    const val APPOINTMENT_1H = "appointment_1h"
    const val MEDICINE_REMINDER = "medicine"
    const val BOOKING_CONFIRMATION = "booking_confirmation"
    const val APPOINTMENT_CANCELLED = "appointment_cancelled"
    const val APPOINTMENT_RESCHEDULED = "appointment_rescheduled"

    // ============ Related Types ============
    const val RELATED_TYPE_APPOINTMENT = "appointment"
    const val RELATED_TYPE_MEDICINE = "medicine"

    // ============ Notification Channels ============
    const val CHANNEL_ID_APPOINTMENTS = "appointments_channel"
    const val CHANNEL_ID_MEDICINES = "medicines_channel"
    const val CHANNEL_ID_CONFIRMATIONS = "confirmations_channel"

    const val CHANNEL_NAME_APPOINTMENTS = "Appointment Reminders"
    const val CHANNEL_NAME_MEDICINES = "Medicine Reminders"
    const val CHANNEL_NAME_CONFIRMATIONS = "Booking Confirmations"

    const val CHANNEL_DESC_APPOINTMENTS = "Notifications for upcoming appointments"
    const val CHANNEL_DESC_MEDICINES = "Notifications for medicine intake"
    const val CHANNEL_DESC_CONFIRMATIONS = "Notifications for booking confirmations"

    // ============ WorkManager Tags ============
    const val WORK_TAG_APPOINTMENT_24H = "appointment_reminder_24h"
    const val WORK_TAG_APPOINTMENT_1H = "appointment_reminder_1h"
    const val WORK_TAG_MEDICINE = "medicine_reminder"

    // ============ WorkManager Input Data Keys ============
    const val WORK_DATA_NOTIFICATION_ID = "notificationId"
    const val WORK_DATA_USER_ID = "userId"
    const val WORK_DATA_TYPE = "type"
    const val WORK_DATA_TITLE = "title"
    const val WORK_DATA_MESSAGE = "message"
    const val WORK_DATA_RELATED_ID = "relatedId"
    const val WORK_DATA_RELATED_TYPE = "relatedType"

    // ============ Notification IDs (for Android NotificationManager) ============
    const val NOTIFICATION_ID_APPOINTMENT_BASE = 1000
    const val NOTIFICATION_ID_MEDICINE_BASE = 2000
    const val NOTIFICATION_ID_CONFIRMATION_BASE = 3000

    // ============ Time Constants (in milliseconds) ============
    const val HOURS_24_IN_MILLIS = 24L * 60 * 60 * 1000
    const val HOURS_1_IN_MILLIS = 1L * 60 * 60 * 1000

    // ============ Medicine Frequency ============
    const val FREQUENCY_DAILY = "daily"
    const val FREQUENCY_WEEKLY = "weekly"
    const val FREQUENCY_MONTHLY = "monthly"
    const val FREQUENCY_AS_NEEDED = "as_needed"

    // ============ SharedPreferences Keys for WorkManager IDs ============
    const val PREFS_WORK_IDS = "notification_work_ids"
    const val PREFS_KEY_APPOINTMENT_PREFIX = "appointment_"
    const val PREFS_KEY_MEDICINE_PREFIX = "medicine_"

    // ============ Firebase Paths ============
    const val FIREBASE_MEDICINE_REMINDERS = "medicineReminders"
    const val FIREBASE_NOTIFICATION_PREFERENCES = "notificationPreferences"
    const val FIREBASE_NOTIFICATION_HISTORY = "notificationHistory"

    // ============ Notification Request Codes ============
    const val REQUEST_CODE_APPOINTMENT_24H = 10001
    const val REQUEST_CODE_APPOINTMENT_1H = 10002
    const val REQUEST_CODE_MEDICINE = 20001
}

object NotificationMessages {

    // ============ Appointment Notifications ============

    fun appointment24HourTitle() = "Appointment Tomorrow"

    fun appointment24HourMessage(appointment: AppointmentModel): String {
        val displayDate = DateTimeUtils.formatDateForDisplay(appointment.date)
        val displayTime = DateTimeUtils.formatTimeForDisplay(appointment.time)
        return "You have an appointment with Dr. ${appointment.doctorName} on $displayDate at $displayTime"
    }

    fun appointment1HourTitle() = "Appointment Soon"

    fun appointment1HourMessage(appointment: AppointmentModel): String {
        val displayTime = DateTimeUtils.formatTimeForDisplay(appointment.time)
        return "Your appointment with Dr. ${appointment.doctorName} starts in 1 hour at $displayTime"
    }

    fun bookingConfirmationTitle() = "Appointment Confirmed"

    fun bookingConfirmationMessage(appointment: AppointmentModel): String {
        val displayDate = DateTimeUtils.formatDateForDisplay(appointment.date)
        val displayTime = DateTimeUtils.formatTimeForDisplay(appointment.time)
        return "Your appointment with Dr. ${appointment.doctorName} on $displayDate at $displayTime has been confirmed"
    }

    fun appointmentCancelledTitle() = "Appointment Cancelled"

    fun appointmentCancelledMessage(doctorName: String, date: String, time: String): String {
        val displayDate = DateTimeUtils.formatDateForDisplay(date)
        val displayTime = DateTimeUtils.formatTimeForDisplay(time)
        return "Your appointment with Dr. $doctorName on $displayDate at $displayTime has been cancelled"
    }

    fun appointmentRescheduledTitle() = "Appointment Rescheduled"

    fun appointmentRescheduledMessage(doctorName: String, newDate: String, newTime: String): String {
        val displayDate = DateTimeUtils.formatDateForDisplay(newDate)
        val displayTime = DateTimeUtils.formatTimeForDisplay(newTime)
        return "Your appointment with Dr. $doctorName has been rescheduled to $displayDate at $displayTime"
    }

    // ============ Medicine Notifications ============

    fun medicineReminderTitle() = "Time to Take Medicine"

    fun medicineReminderMessage(medicineName: String, dosage: String): String {
        return "Time to take $medicineName - $dosage"
    }

    fun medicineReminderWithInstructions(medicineName: String, dosage: String, instructions: String): String {
        return "Time to take $medicineName - $dosage. $instructions"
    }

    // ============ Helper to get relative time ============

    fun getRelativeTimeDescription(appointmentDate: String, appointmentTime: String): String {
        val now = System.currentTimeMillis()
        val appointmentTimeMillis = DateTimeUtils.parseDateTime(appointmentDate, appointmentTime)

        val diffMillis = appointmentTimeMillis - now
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            days > 1 -> "in $days days"
            days == 1L -> "tomorrow"
            hours > 1 -> "in $hours hours"
            hours == 1L -> "in 1 hour"
            else -> "soon"
        }
    }
}