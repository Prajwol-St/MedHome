package com.example.medhomeapp.utils

import android.content.Context
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.NotificationRepositoryImpl

object AppointmentNotificationIntegration {

    private val notificationRepository = NotificationRepositoryImpl()

        fun onAppointmentBooked(
        context: Context,
        appointment: AppointmentModel,
        onComplete: (Boolean, String) -> Unit
    ) {
        // Send booking confirmation immediately
        notificationRepository.sendBookingConfirmation(appointment) { success, message ->
            if (success) {
                // Show Android notification
                val notificationId = NotificationHelper.generateNotificationId(
                    NotificationConstants.BOOKING_CONFIRMATION,
                    appointment.appointmentId
                )
                NotificationHelper.showBookingConfirmationNotification(
                    context = context,
                    notificationId = notificationId,
                    title = "Appointment Confirmed",
                    message = "Your appointment with Dr. ${appointment.doctorName} on ${appointment.date} at ${appointment.time} has been confirmed."
                )

                // Schedule reminders
                scheduleReminders(context, appointment, onComplete)
            } else {
                onComplete(false, message)
            }
        }
    }

        private fun scheduleReminders(
        context: Context,
        appointment: AppointmentModel,
        onComplete: (Boolean, String) -> Unit
    ) {
        notificationRepository.scheduleAppointmentReminders(appointment) { success, message ->
            if (success) {
                // Schedule WorkManager tasks
                NotificationScheduler.scheduleAppointmentReminders(context, appointment)
                onComplete(true, "Reminders scheduled successfully")
            } else {
                // Don't fail the whole operation if reminders can't be scheduled
                onComplete(true, "Appointment booked (reminders: $message)")
            }
        }
    }

        fun onAppointmentCancelled(
        context: Context,
        appointmentId: String
    ) {
        // Cancel scheduled reminders
        NotificationScheduler.cancelAppointmentReminders(context, appointmentId)

        // Update repository
        notificationRepository.cancelAppointmentReminders(appointmentId) { _, _ ->
            // Silently handle
        }
    }

        fun onAppointmentRescheduled(
        context: Context,
        oldAppointmentId: String,
        newAppointment: AppointmentModel,
        onComplete: (Boolean, String) -> Unit
    ) {
        // Cancel old reminders
        onAppointmentCancelled(context, oldAppointmentId)

        // Schedule new reminders (without booking confirmation)
        notificationRepository.scheduleAppointmentReminders(newAppointment) { success, message ->
            if (success) {
                NotificationScheduler.scheduleAppointmentReminders(context, newAppointment)
                onComplete(true, "Appointment rescheduled and reminders updated")
            } else {
                onComplete(true, "Appointment rescheduled but reminders not updated: $message")
            }
        }
    }
}

object MedicineNotificationIntegration {

        fun onMedicineAdded(
        context: Context,
        medicine: com.example.medhomeapp.model.MedicineReminderModel
    ) {
        if (medicine.isEnabled) {
            NotificationScheduler.scheduleMedicineReminders(context, medicine)
        }
    }

        fun onMedicineUpdated(
        context: Context,
        medicine: com.example.medhomeapp.model.MedicineReminderModel
    ) {
        // Cancel old reminders
        NotificationScheduler.cancelMedicineReminders(context, medicine.medicineId)

        // Schedule new ones if enabled
        if (medicine.isEnabled) {
            NotificationScheduler.scheduleMedicineReminders(context, medicine)
        }
    }

        fun onMedicineDeleted(
        context: Context,
        medicineId: String
    ) {
        NotificationScheduler.cancelMedicineReminders(context, medicineId)
    }

        fun onMedicineToggled(
        context: Context,
        medicine: com.example.medhomeapp.model.MedicineReminderModel,
        isEnabled: Boolean
    ) {
        if (isEnabled) {
            NotificationScheduler.scheduleMedicineReminders(context, medicine)
        } else {
            NotificationScheduler.cancelMedicineReminders(context, medicine.medicineId)
        }
    }

        fun onGlobalToggle(
        context: Context,
        isEnabled: Boolean
    ) {
        if (!isEnabled) {
            // Cancel all medicine reminders
            NotificationScheduler.cancelAllMedicineReminders(context)
        }
        // If enabled, reminders will be scheduled when user opens medicine list
    }
}