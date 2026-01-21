package com.example.medhomeapp.model

data class ScheduledNotificationWorkModel(
    val referenceId: String,
    val workIds: List<String>,
    val type: String,
    val scheduledFor: Long
) {
    fun toJson(): String {
        return """{"referenceId":"$referenceId","workIds":${workIds.joinToString(",", "[", "]") { "\"$it\"" }},"type":"$type","scheduledFor":$scheduledFor}"""
    }
}

