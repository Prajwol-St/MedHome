import com.example.medhomeapp.model.AppointmentModel

interface AppointmentRepo {

    fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAppointmentsByDoctor(
        doctorId: String,
        callback: (List<AppointmentModel>?, String?) -> Unit
    )

    fun markDoctorLeave(
        doctorId: String,
        dateMillis: Long,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUserId(): String?

    // 🔴 FIXED
    fun getCurrentUserRole(callback: (String?) -> Unit)
}
