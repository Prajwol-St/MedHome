package com.example.medhomeapp.repository

import com.google.firebase.database.FirebaseDatabase

class DoctorAvailabilityRepoImpl : DoctorAvailabilityRepo {

    private val database = FirebaseDatabase.getInstance()
    private val availabilityRef = database.getReference("availability")

    /* ---------------- ADD AVAILABILITY ---------------- */

    override fun addAvailability(
        doctorId: String,
        date: String,
        time: String,
        callback: (Boolean, String) -> Unit
    ) {
        availabilityRef
            .child(doctorId)
            .child(date)
            .child(time)
            .setValue(true)
            .addOnSuccessListener {
                callback(true, "Availability added successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to add availability")
            }
    }

    /* ---------------- GET AVAILABILITY ---------------- */

    override fun getAvailability(
        doctorId: String,
        date: String,
        callback: (List<String>) -> Unit
    ) {
        availabilityRef
            .child(doctorId)
            .child(date)
            .get()
            .addOnSuccessListener { snapshot ->
                val timeSlots = snapshot.children.mapNotNull { it.key }
                callback(timeSlots)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    /* ---------------- REMOVE AVAILABILITY ---------------- */

    override fun removeAvailability(
        doctorId: String,
        date: String,
        time: String,
        callback: (Boolean, String) -> Unit
    ) {
        availabilityRef
            .child(doctorId)
            .child(date)
            .child(time)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Availability removed successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to remove availability")
            }
    }
}
