package com.example.medhomeapp.utils

import android.content.Context
import androidx.work.*
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.model.extensions.get24HourReminderTime
import com.example.medhomeapp.model.extensions.get1HourReminderTime
import com.example.medhomeapp.workers.AppointmentReminderWorker
import com.example.medhomeapp.workers.MedicineReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {

        fun scheduleAppointmentReminders(
        context: Context,
        appointment: AppointmentModel
    ): Pair<UUID, UUID> {
        val now = System.currentTimeMillis()

        val reminder24hTime = appointment.get24HourReminderTime()
        val reminder1hTime = appointment.get1HourReminderTime()

        // Schedule 24-hour reminder
        val delay24h = maxOf(0, reminder24hTime - now)
        val work24h = createAppointmentReminderWork(
            appointment = appointment,
            type = NotificationConstants.APPOINTMENT_24H,
            delay = delay24h
        )

        WorkManager.getInstance(context).enqueueUniqueWork(
            "${NotificationConstants.WORK_TAG_APPOINTMENT_24H}_${appointment.appointmentId}",
            ExistingWorkPolicy.REPLACE,
            work24h
        )

        // Schedule 1-hour reminder
        val delay1h = maxOf(0, reminder1hTime - now)
        val work1h = createAppointmentReminderWork(
            appointment = appointment,
            type = NotificationConstants.APPOINTMENT_1H,
            delay = delay1h
        )

        WorkManager.getInstance(context).enqueueUniqueWork(
            "${NotificationConstants.WORK_TAG_APPOINTMENT_1H}_${appointment.appointmentId}",
            ExistingWorkPolicy.REPLACE,
            work1h
        )

        return Pair(work24h.id, work1h.id)
    }

        private fun createAppointmentReminderWork(
        appointment: AppointmentModel,
        type: String,
        delay: Long
    ): OneTimeWorkRequest {
        val inputData = Data.Builder()
            .putString(NotificationConstants.WORK_DATA_USER_ID, appointment.patientId)
            .putString(NotificationConstants.WORK_DATA_TYPE, type)
            .putString(NotificationConstants.WORK_DATA_RELATED_ID, appointment.appointmentId)
            .build()

        return OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("${type}_${appointment.appointmentId}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
    }

        fun cancelAppointmentReminders(context: Context, appointmentId: String) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork("${NotificationConstants.WORK_TAG_APPOINTMENT_24H}_${appointmentId}")
            cancelUniqueWork("${NotificationConstants.WORK_TAG_APPOINTMENT_1H}_${appointmentId}")
        }
    }

        fun scheduleMedicineReminders(
        context: Context,
        medicine: MedicineReminderModel
    ): List<UUID> {
        val workIds = mutableListOf<UUID>()

        medicine.reminderTimes.forEach { time ->
            val workId = scheduleMedicineReminderForTime(context, medicine, time)
            workIds.add(workId)
        }

        return workIds
    }

        private fun scheduleMedicineReminderForTime(
        context: Context,
        medicine: MedicineReminderModel,
        time: String
    ): UUID {
        val now = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            val parts = time.split(":")
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = scheduledTime.timeInMillis - now.timeInMillis

        val inputData = Data.Builder()
            .putString(NotificationConstants.WORK_DATA_USER_ID, medicine.userId)
            .putString(NotificationConstants.WORK_DATA_RELATED_ID, medicine.medicineId)
            .putString(NotificationConstants.WORK_DATA_TYPE, NotificationConstants.MEDICINE_REMINDER)
            .build()

        val workRequest = if (medicine.frequency == NotificationConstants.FREQUENCY_DAILY) {
            // Daily recurring reminder
            PeriodicWorkRequestBuilder<MedicineReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("${NotificationConstants.WORK_TAG_MEDICINE}_${medicine.medicineId}")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()
        } else {
            // One-time reminder (for as-needed medicines)
            OneTimeWorkRequestBuilder<MedicineReminderWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("${NotificationConstants.WORK_TAG_MEDICINE}_${medicine.medicineId}")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()
        }

        WorkManager.getInstance(context).enqueueUniqueWork(
            "${NotificationConstants.WORK_TAG_MEDICINE}_${medicine.medicineId}_${time}",
            ExistingWorkPolicy.REPLACE,
            workRequest as OneTimeWorkRequest
        )

        return workRequest.id
    }

        fun cancelMedicineReminders(context: Context, medicineId: String) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("${NotificationConstants.WORK_TAG_MEDICINE}_${medicineId}")
    }

        fun cancelAllMedicineReminders(context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(NotificationConstants.WORK_TAG_MEDICINE)
    }

        fun cancelAllAppointmentReminders(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelAllWorkByTag(NotificationConstants.WORK_TAG_APPOINTMENT_24H)
            cancelAllWorkByTag(NotificationConstants.WORK_TAG_APPOINTMENT_1H)
        }
    }
}