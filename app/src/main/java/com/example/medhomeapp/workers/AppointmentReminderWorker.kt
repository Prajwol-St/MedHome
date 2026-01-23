package com.example.medhomeapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.repository.AppointmentBookingRepoImpl
import com.example.medhomeapp.repository.NotificationRepositoryImpl
import com.example.medhomeapp.utils.NotificationConstants
import com.example.medhomeapp.utils.NotificationHelper
import com.example.medhomeapp.utils.NotificationMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class AppointmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationRepository = NotificationRepositoryImpl()
    private val appointmentRepository = AppointmentBookingRepoImpl(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get data from WorkManager
            val appointmentId = inputData.getString(NotificationConstants.WORK_DATA_RELATED_ID)
                ?: return@withContext Result.failure()
            val userId = inputData.getString(NotificationConstants.WORK_DATA_USER_ID)
                ?: return@withContext Result.failure()
            val type = inputData.getString(NotificationConstants.WORK_DATA_TYPE)
                ?: return@withContext Result.failure()

            // Fetch latest appointment data from Firebase
            val appointment = suspendCancellableCoroutine<AppointmentModel?> { continuation ->
                appointmentRepository.getAppointmentById(appointmentId) { appt ->
                    continuation.resume(appt)
                }
            }

            if (appointment == null) {
                return@withContext Result.failure()
            }

            // Check if appointment reminders are enabled
            val remindersEnabled = suspendCancellableCoroutine { continuation ->
                notificationRepository.areAppointmentRemindersEnabled(userId) { enabled ->
                    continuation.resume(enabled)
                }
            }

            if (!remindersEnabled) {
                return@withContext Result.success()
            }

            // Check if appointment is still upcoming
            if (appointment.status != "pending" && appointment.status != "scheduled") {
                return@withContext Result.success()
            }

            // Generate notification title and message based on type
            val title = when (type) {
                NotificationConstants.APPOINTMENT_24H -> NotificationMessages.appointment24HourTitle()
                NotificationConstants.APPOINTMENT_1H -> NotificationMessages.appointment1HourTitle()
                else -> "Appointment Reminder"
            }

            val message = when (type) {
                NotificationConstants.APPOINTMENT_24H -> NotificationMessages.appointment24HourMessage(appointment)
                NotificationConstants.APPOINTMENT_1H -> NotificationMessages.appointment1HourMessage(appointment)
                else -> "You have an upcoming appointment"
            }

            // Show notification
            val androidNotificationId = NotificationHelper.generateNotificationId(type, appointmentId)
            NotificationHelper.showAppointmentNotification(
                context = applicationContext,
                notificationId = androidNotificationId,
                title = title,
                message = message,
                appointmentId = appointmentId
            )

            // Save to notification history
            val notification = NotificationHistoryModel(
                notificationId = "${appointmentId}_${type}_${System.currentTimeMillis()}",
                userId = userId,
                type = type,
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                relatedId = appointmentId,
                relatedType = NotificationConstants.RELATED_TYPE_APPOINTMENT
            )

            val saved = suspendCancellableCoroutine { continuation ->
                notificationRepository.addNotificationToHistory(notification) { success, _ ->
                    continuation.resume(success)
                }
            }

            if (saved) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}