package com.example.medhomeapp.repository

import AppointmentModel
import AppointmentRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AppointmentRepoImpl : AppointmentRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val appointmentsRef = database.getReference("appointments")
    private val usersRef = database.getReference("users")
    private val doctorLeavesRef = database.getReference("doctorLeaves")

    /* ---------------- ADD APPOINTMENT ---------------- */

    override fun addAppointment(
        appointment: AppointmentModel,
        callback: (Boolean, String) -> Unit
    ) {
        val key = appointmentsRef.push().key

        if (key == null) {
            callback(false, "Failed to generate appointment ID")
            return
        }

        // ✅ FIXED: use `id`
        val newAppointment = appointment.copy(appointmentId = key)

        appointmentsRef.child(key)
            .setValue(newAppointment)
            .addOnSuccessListener {
                callback(true, "Appointment booked successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to book appointment")
            }
    }

    /* ---------------- GET APPOINTMENTS BY DOCTOR ---------------- */

    override fun getAppointmentsByDoctor(
        doctorId: String,
        callback: (List<AppointmentModel>?, String?) -> Unit
    ) {
        appointmentsRef
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<AppointmentModel>()

                    for (child in snapshot.children) {
                        val appointment =
                            child.getValue(AppointmentModel::class.java)
                        appointment?.let { list.add(it) }
                    }

                    callback(list, null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.message)
                }
            })
    }




    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    override fun getCurrentUserRole(
        callback: (String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            callback(null)
            return
        }

        usersRef.child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.getValue(String::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
}
