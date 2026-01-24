package com.example.medhomeapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medhomeapp.R
import com.example.medhomeapp.view.MainActivity
import com.example.medhomeapp.view.NotificationHistoryActivity

object NotificationHelper {

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Appointment Reminders Channel
            val appointmentChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_APPOINTMENTS,
                NotificationConstants.CHANNEL_NAME_APPOINTMENTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationConstants.CHANNEL_DESC_APPOINTMENTS
                enableLights(true)
                enableVibration(true)
            }

            // Medicine Reminders Channel
            val medicineChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_MEDICINES,
                NotificationConstants.CHANNEL_NAME_MEDICINES,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationConstants.CHANNEL_DESC_MEDICINES
                enableLights(true)
                enableVibration(true)
            }

            // Booking Confirmations Channel
            val confirmationChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_CONFIRMATIONS,
                NotificationConstants.CHANNEL_NAME_CONFIRMATIONS,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConstants.CHANNEL_DESC_CONFIRMATIONS
                enableLights(true)
            }

            // Register channels
            notificationManager.createNotificationChannel(appointmentChannel)
            notificationManager.createNotificationChannel(medicineChannel)
            notificationManager.createNotificationChannel(confirmationChannel)
        }
    }

        fun showAppointmentNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        appointmentId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("appointmentId", appointmentId)
            putExtra("openAppointments", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID_APPOINTMENTS)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle permission denial
            e.printStackTrace()
        }
    }

        fun showMedicineNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        medicineId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medicineId", medicineId)
            putExtra("openMedicines", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID_MEDICINES)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

        fun showBookingConfirmationNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, NotificationHistoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID_CONFIRMATIONS)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

        fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

        fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

        fun generateNotificationId(type: String, referenceId: String): Int {
        val baseId = when (type) {
            NotificationConstants.APPOINTMENT_24H,
            NotificationConstants.APPOINTMENT_1H -> NotificationConstants.NOTIFICATION_ID_APPOINTMENT_BASE
            NotificationConstants.MEDICINE_REMINDER -> NotificationConstants.NOTIFICATION_ID_MEDICINE_BASE
            NotificationConstants.BOOKING_CONFIRMATION -> NotificationConstants.NOTIFICATION_ID_CONFIRMATION_BASE
            else -> 1000
        }
        return baseId + referenceId.hashCode() % 1000
    }
}