

interface AppointmentRepo {

    fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAppointmentsByDoctor(
        doctorId: String,
        callback: (List<AppointmentModel>?, String?) -> Unit
    )


    fun getCurrentUserId(): String?

    fun getCurrentUserRole(callback: (String?) -> Unit)
}
