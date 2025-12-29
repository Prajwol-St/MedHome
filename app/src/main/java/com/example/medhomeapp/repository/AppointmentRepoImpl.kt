package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel
import com.google.firebase.database.FirebaseDatabase

class AppointmentRepoImpl {
    private val database = FirebaseDatabase.getInstance()
    private val appointmentsRef = database.getReference("appointments")



    fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ){
        if (userId.isEmpty()) {
            onError(Exception("User not authenticated"))
            return
        }
    }

    fun updateAppointment(
        appointmentId: String,
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ){}

    fun deleteAppointment(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    ){}


    fun getAppointmentsByPatientId(
        patientId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit
    ){

    }


    fun getAppointmentsByDoctorId(
        doctorId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit
    ){

    }
}