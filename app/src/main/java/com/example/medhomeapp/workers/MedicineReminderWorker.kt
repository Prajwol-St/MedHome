package com.example.medhomeapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.repository.NotificationRepositoryImpl
import com.example.medhomeapp.utils.NotificationConstants
import com.example.medhomeapp.utils.NotificationHelper
import com.example.medhomeapp.utils.NotificationMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MedicineReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = NotificationRepositoryImpl()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get data from WorkManager
            val userId = inputData.getString(NotificationConstants.WORK_DATA_USER_ID)
                ?: return@withContext Result.failure()
            val medicineId = inputData.getString(NotificationConstants.WORK_DATA_RELATED_ID)
                ?: return@withContext Result.failure()

            // Check if medicine reminders are globally enabled
            val globallyEnabled = suspendCancellableCoroutine { continuation ->
                repository.areMedicineRemindersEnabled(userId) { enabled ->
                    continuation.resume(enabled)
                }
            }

            if (!globallyEnabled) {
                return@withContext Result.success()
            }

            // Get medicine details
            val medicine = suspendCancellableCoroutine<MedicineReminderModel?> { continuation ->
                repository.getMedicineReminder(userId, medicineId) { med ->
                    continuation.resume(med)
                }
            }

            if (medicine == null || !medicine.isEnabled) {
                return@withContext Result.success()
            }

            // Check if medicine is within active date range
            val now = System.currentTimeMillis()
            if (now < medicine.startDate) {
                return@withContext Result.success()
            }
            if (medicine.endDate != null && now > medicine.endDate) {
                return@withContext Result.success()
            }

            // Create notification message
            val title = NotificationMessages.medicineReminderTitle()
            val message = if (medicine.instructions.isNotEmpty()) {
                NotificationMessages.medicineReminderWithInstructions(
                    medicine.medicineName,
                    medicine.dosage,
                    medicine.instructions
                )
            } else {
                NotificationMessages.medicineReminderMessage(
                    medicine.medicineName,
                    medicine.dosage
                )
            }

            // Show notification
            val androidNotificationId = NotificationHelper.generateNotificationId(
                NotificationConstants.MEDICINE_REMINDER,
                medicineId
            )
            NotificationHelper.showMedicineNotification(
                context = applicationContext,
                notificationId = androidNotificationId,
                title = title,
                message = message,
                medicineId = medicineId
            )

            // Save to notification history
            val notification = NotificationHistoryModel(
                notificationId = "${medicineId}_${System.currentTimeMillis()}",
                userId = userId,
                type = NotificationConstants.MEDICINE_REMINDER,
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                relatedId = medicineId,
                relatedType = NotificationConstants.RELATED_TYPE_MEDICINE
            )

            val saved = suspendCancellableCoroutine { continuation ->
                repository.addNotificationToHistory(notification) { success, _ ->
                    continuation.resume(success)
                }
            }

            // If this is a daily reminder, reschedule for tomorrow
            if (medicine.frequency == "daily") {
                com.example.medhomeapp.utils.NotificationScheduler.scheduleMedicineReminders(
                    applicationContext,
                    medicine
                )
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