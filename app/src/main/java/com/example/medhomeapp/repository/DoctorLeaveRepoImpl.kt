package com.example.medhomeapp.repository

import com.google.firebase.database.FirebaseDatabase

class DoctorLeaveRepoImpl : DoctorLeaveRepo {

    private val database = FirebaseDatabase.getInstance()
    private val doctorLeavesRef = database.getReference("doctorLeaves")

    /* ---------------- ADD DOCTOR LEAVE ---------------- */

    fun addLeave(
        doctorId: String,
        date: String,
        callback: (Boolean, String) -> Unit
    ) {
        doctorLeavesRef
            .child(doctorId)
            .child(date)
            .setValue(true)
            .addOnSuccessListener {
                callback(true, "Leave added successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to add leave")
            }
    }

    /* ---------------- CHECK DOCTOR LEAVE ---------------- */

    fun isDoctorOnLeave(
        doctorId: String,
        date: String,
        callback: (Boolean) -> Unit
    ) {
        doctorLeavesRef
            .child(doctorId)
            .child(date)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
