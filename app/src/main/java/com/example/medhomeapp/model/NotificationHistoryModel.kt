package com.example.medhomeapp.model

data class NotificationHistoryModel(
    val notificationId: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedId: String = "",
    val relatedType: String = "",
    val actionUrl: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "notificationId" to notificationId,
        "userId" to userId,
        "type" to type,
        "title" to title,
        "message" to message,
        "timestamp" to timestamp,
        "isRead" to isRead,
        "relatedId" to relatedId,
        "relatedType" to relatedType,
        "actionUrl" to actionUrl
    )
}

