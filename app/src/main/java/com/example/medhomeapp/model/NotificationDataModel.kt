package com.example.medhomeapp.model

data class NotificationDataModel(
    val notificationId: String,
    val userId: String,
    val type: String,
    val title: String,
    val message: String,
    val relatedId: String,
    val relatedType: String
) {
    fun toWorkDataMap(): Map<String, String> = mapOf(
        "notificationId" to notificationId,
        "userId" to userId,
        "type" to type,
        "title" to title,
        "message" to message,
        "relatedId" to relatedId,
        "relatedType" to relatedType
    )
}

