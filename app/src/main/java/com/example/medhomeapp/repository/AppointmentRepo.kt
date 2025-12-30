package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel

interface AppointmentRepo {
    fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun updateAppointment(
        appointmentId: String,
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteAppointment(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    )


    fun getAppointmentsByPatientId(
        patientId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit)


    fun getAppointmentsByDoctorId(
        doctorId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit
    )


    fun getCurrentUserId(): String?

}