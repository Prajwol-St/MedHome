package com.example.medhomeapp.repository

import com.google.firebase.database.FirebaseDatabase

class DoctorLeaveRepoImpl : DoctorLeaveRepo {

    private val database = FirebaseDatabase.getInstance()
    private val doctorLeavesRef = database.getReference("doctorLeaves")

    /* ---------------- ADD DOCTOR LEAVE ---------------- */

    override fun addLeave(
        doctorId: String,
        date: String,
        callback: (Boolean, String) -> Unit
    ) {
        doctorLeavesRef.child(doctorId).child(date).setValue(true)
            .addOnSuccessListener {
                callback(true, "Leave added successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed")
            }
    }

    /* ---------------- CHECK DOCTOR LEAVE ---------------- */

    override fun isDoctorOnLeave(
        doctorId: String,
        date: String,
        callback: (Boolean) -> Unit
    ) {
        doctorLeavesRef.child(doctorId).child(date).get()
            .addOnSuccessListener { callback(it.exists()) }
            .addOnFailureListener { callback(false) }
    }
}
