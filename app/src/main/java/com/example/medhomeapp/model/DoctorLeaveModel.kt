package com.example.medhomeapp.model

data class DoctorLeaveModel(
    val leaveId: String = "",
    val doctorId: String = "",
    val startDate: String = "", // yyyy-MM-dd
    val endDate: String = "", // yyyy-MM-dd
    val reason: String = "",
    val leaveType: String = "personal", // personal, medical, conference, vacation
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "leaveId" to leaveId,
        "doctorId" to doctorId,
        "startDate" to startDate,
        "endDate" to endDate,
        "reason" to reason,
        "leaveType" to leaveType,
        "isActive" to isActive,
        "createdAt" to createdAt
    )
}