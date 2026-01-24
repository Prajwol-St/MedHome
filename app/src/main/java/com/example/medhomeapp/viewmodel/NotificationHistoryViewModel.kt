package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.repository.NotificationRepository

class NotificationHistoryViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications = mutableStateOf<List<NotificationHistoryModel>>(emptyList())
    val unreadCount = mutableStateOf(0)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

        fun loadNotifications(userId: String) {
        isLoading.value = true
        errorMessage.value = null

        // Load notification history
        repository.getNotificationHistory(userId) { notificationList ->
            notifications.value = notificationList
            isLoading.value = false
        }

        // Load unread count
        repository.getUnreadNotificationCount(userId) { count ->
            unreadCount.value = count
        }
    }

        fun markAsRead(userId: String, notificationId: String) {
        repository.markNotificationAsRead(userId, notificationId) { success, message ->
            if (success) {
                // Update local list immediately for better UX
                notifications.value = notifications.value.map { notification ->
                    if (notification.notificationId == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }
                // Decrease unread count
                if (unreadCount.value > 0) {
                    unreadCount.value = unreadCount.value - 1
                }
            } else {
                errorMessage.value = message
            }
        }
    }

        fun markAllAsRead(userId: String) {
        repository.markAllNotificationsAsRead(userId) { success, message ->
            if (success) {
                // Update local list
                notifications.value = notifications.value.map { it.copy(isRead = true) }
                unreadCount.value = 0
            } else {
                errorMessage.value = message
            }
        }
    }

        fun deleteNotification(userId: String, notificationId: String) {
        repository.deleteNotificationFromHistory(userId, notificationId) { success, message ->
            if (success) {
                // Remove from local list
                val deletedNotification = notifications.value.find { it.notificationId == notificationId }
                notifications.value = notifications.value.filter { it.notificationId != notificationId }

                // Update unread count if deleted notification was unread
                if (deletedNotification?.isRead == false) {
                    unreadCount.value = maxOf(0, unreadCount.value - 1)
                }
            } else {
                errorMessage.value = message
            }
        }
    }

        fun getNotificationsByType(type: String): List<NotificationHistoryModel> {
        return notifications.value.filter { it.type == type }
    }

        fun getUnreadNotifications(): List<NotificationHistoryModel> {
        return notifications.value.filter { !it.isRead }
    }

        fun getReadNotifications(): List<NotificationHistoryModel> {
        return notifications.value.filter { it.isRead }
    }

        fun getTodaysNotifications(): List<NotificationHistoryModel> {
        val startOfDay = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        return notifications.value.filter { it.timestamp >= startOfDay }
    }

        fun clearError() {
        errorMessage.value = null
    }
}