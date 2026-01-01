data class AppointmentModel(
    val appointmentId: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val patientName: String = "",
    val date: String = "",
    val time: String = "",
    val reason: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "appointmentId" to appointmentId,
        "patientId" to patientId,
        "doctorId" to doctorId,
        "patientName" to patientName,
        "date" to date,
        "time" to time,
        "reason" to reason
    )
}
