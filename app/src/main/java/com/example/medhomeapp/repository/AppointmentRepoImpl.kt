package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AppointmentRepoImpl : AppointmentRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val appointmentsRef = database.getReference("appointments")

    /* ---------------- ADD APPOINTMENT ---------------- */

    override fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ) {
        val appointmentId = appointmentsRef.push().key

        if (appointmentId == null) {
            callback(false, "Failed to generate appointment ID")
            return
        }

        val newAppointment = appointment.copy(appointmentId = appointmentId)

        appointmentsRef.child(appointmentId)
            .setValue(newAppointment)
            .addOnSuccessListener {
                callback(true, "Appointment added successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to add appointment")
            }
    }



    override fun updateAppointment(
        appointmentId: String,
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ) {
        appointmentsRef.child(appointmentId)
            .setValue(appointment)
            .addOnSuccessListener {
                callback(true, "Appointment updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to update appointment")
            }
    }

    /* ---------------- DELETE APPOINTMENT ---------------- */

    override fun deleteAppointment(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    ) {
        appointmentsRef.child(appointmentId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Appointment deleted successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to delete appointment")
            }
    }

    /* ---------------- GET BY PATIENT ID ---------------- */

    override fun getAppointmentsByPatientId(
        patientId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit
    ) {
        appointmentsRef
            .orderByChild("patientId")
            .equalTo(patientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<AppointmentModel>()
                    for (data in snapshot.children) {
                        val appointment = data.getValue(AppointmentModel::class.java)
                        if (appointment != null) list.add(appointment)
                    }
                    callback(true, "Success", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    /* ---------------- GET BY DOCTOR ID ---------------- */

    override fun getAppointmentsByDoctorId(
        doctorId: String,
        callback: (Boolean, String, List<AppointmentModel>) -> Unit
    ) {
        appointmentsRef
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<AppointmentModel>()
                    for (data in snapshot.children) {
                        val appointment = data.getValue(AppointmentModel::class.java)
                        if (appointment != null) list.add(appointment)
                    }
                    callback(true, "Success", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
