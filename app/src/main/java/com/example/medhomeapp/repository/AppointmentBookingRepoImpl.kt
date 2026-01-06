package com.example.medhomeapp.repository

import AppointmentModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class AppointmentBookingRepoImpl : AppointmentBookingRepo {

    private val db = FirebaseDatabase.getInstance()
    private val availabilityRef = db.getReference("doctor_availability")
    private val appointmentRef = db.getReference("appointments")

    override fun bookAppointment(
        slot: TimeSlot,
        patient: UserModel,
        reason: String,
        callback: (Boolean, String) -> Unit
    ) {
        val slotRef =
            availabilityRef.child(slot.doctorId).child(slot.id)

        slotRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentSlot = currentData.getValue(TimeSlot::class.java)

                if (currentSlot == null || !currentSlot.isAvailable) {
                    return Transaction.abort()
                }

                currentData.child("isAvailable").value = false
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (!committed) {
                    callback(false, "Time slot already booked")
                    return
                }

                val appointmentId = appointmentRef.push().key ?: return

                val appointment = AppointmentModel(
                    appointmentId = appointmentId,
                    patientId = patient.id,
                    doctorId = slot.doctorId,
                    patientName = patient.name,
                    date = slot.day,
                    time = "${slot.startTime} - ${slot.endTime}",
                    reason = reason
                )

                appointmentRef.child(appointmentId)
                    .setValue(appointment.toMap())
                    .addOnSuccessListener {
                        callback(true, "Appointment booked successfully")
                    }
                    .addOnFailureListener {
                        callback(false, "Failed to book appointment")
                    }
            }
        })
    }
}
