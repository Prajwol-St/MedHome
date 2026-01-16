package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.utils.AppConstants
import com.google.firebase.database.*

class AppointmentManagementRepoImpl : AppointmentManagementRepo {

    private val db = FirebaseDatabase.getInstance()
    private val appointmentRef = db.getReference("appointments")
    private val appointmentsByPatientRef = db.getReference("appointmentsByPatient")
    private val appointmentsByDoctorRef = db.getReference("appointmentsByDoctor")
    private val availabilityRef = db.getReference("doctor_availability")

    override fun getUpcomingAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    ) {
        val indexRef = if (role == AppConstants.ROLE_PATIENT) {
            appointmentsByPatientRef.child(userId).child("upcoming")
        } else {
            appointmentsByDoctorRef.child(userId).child("upcoming")
        }

        indexRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<AppointmentModel>()
                val appointmentIds = snapshot.children.mapNotNull { it.key }

                if (appointmentIds.isEmpty()) {
                    callback(emptyList())
                    return
                }

                var fetchedCount = 0
                appointmentIds.forEach { appointmentId ->
                    appointmentRef.child(appointmentId).get()
                        .addOnSuccessListener { appointmentSnapshot ->
                            val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                            if (appointment != null) {
                                appointments.add(appointment)
                            }
                            fetchedCount++
                            if (fetchedCount == appointmentIds.size) {
                                // Sort by date and time
                                callback(appointments.sortedBy { "${it.date} ${it.time}" })
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    override fun getPastAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    ) {
        val indexRef = if (role == AppConstants.ROLE_PATIENT) {
            appointmentsByPatientRef.child(userId).child("past")
        } else {
            appointmentsByDoctorRef.child(userId).child("past")
        }

        indexRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<AppointmentModel>()
                val appointmentIds = snapshot.children.mapNotNull { it.key }

                if (appointmentIds.isEmpty()) {
                    callback(emptyList())
                    return
                }

                var fetchedCount = 0
                appointmentIds.forEach { appointmentId ->
                    appointmentRef.child(appointmentId).get()
                        .addOnSuccessListener { appointmentSnapshot ->
                            val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                            if (appointment != null) {
                                appointments.add(appointment)
                            }
                            fetchedCount++
                            if (fetchedCount == appointmentIds.size) {
                                // Sort by date descending (newest first)
                                callback(appointments.sortedByDescending { "${it.date} ${it.time}" })
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    override fun getCancelledAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    ) {
        val indexRef = if (role == AppConstants.ROLE_PATIENT) {
            appointmentsByPatientRef.child(userId).child("cancelled")
        } else {
            appointmentsByDoctorRef.child(userId).child("cancelled")
        }

        indexRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<AppointmentModel>()
                val appointmentIds = snapshot.children.mapNotNull { it.key }

                if (appointmentIds.isEmpty()) {
                    callback(emptyList())
                    return
                }

                var fetchedCount = 0
                appointmentIds.forEach { appointmentId ->
                    appointmentRef.child(appointmentId).get()
                        .addOnSuccessListener { appointmentSnapshot ->
                            val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                            if (appointment != null) {
                                appointments.add(appointment)
                            }
                            fetchedCount++
                            if (fetchedCount == appointmentIds.size) {
                                callback(appointments.sortedByDescending { it.cancelledAt })
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    override fun cancelAppointment(
        appointmentId: String,
        reason: String,
        cancelledBy: String,
        callback: (Boolean, String) -> Unit
    ) {
        // First, check if cancellation is allowed
        canCancelAppointment(appointmentId) { allowed, message ->
            if (!allowed) {
                callback(false, message)
                return@canCancelAppointment
            }

            // Fetch appointment details
            appointmentRef.child(appointmentId).get()
                .addOnSuccessListener { snapshot ->
                    val appointment = snapshot.getValue(AppointmentModel::class.java)

                    if (appointment == null) {
                        callback(false, "Appointment not found")
                        return@addOnSuccessListener
                    }

                    // Update appointment status
                    val updates = mapOf(
                        "status" to AppConstants.STATUS_CANCELLED,
                        "cancellationReason" to reason,
                        "cancelledBy" to cancelledBy,
                        "cancelledAt" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )

                    appointmentRef.child(appointmentId).updateChildren(updates)
                        .addOnSuccessListener {
                            // Update indexes
                            moveAppointmentIndex(
                                appointmentId,
                                appointment.patientId,
                                appointment.doctorId,
                                "upcoming",
                                "cancelled"
                            )

                            // Free up the time slot
                            freeTimeSlot(
                                appointment.doctorId,
                                appointment.date,
                                appointment.time
                            )

                            callback(true, "Appointment cancelled successfully")
                        }
                        .addOnFailureListener {
                            callback(false, "Failed to cancel appointment: ${it.message}")
                        }
                }
                .addOnFailureListener {
                    callback(false, "Failed to fetch appointment: ${it.message}")
                }
        }
    }

    override fun rescheduleAppointment(
        appointmentId: String,
        newDate: String,
        newTime: String,
        newSlotId: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Fetch current appointment
        appointmentRef.child(appointmentId).get()
            .addOnSuccessListener { snapshot ->
                val appointment = snapshot.getValue(AppointmentModel::class.java)

                if (appointment == null) {
                    callback(false, "Appointment not found")
                    return@addOnSuccessListener
                }

                // Check if the new slot is available
                availabilityRef
                    .child(appointment.doctorId)
                    .child(newDate)
                    .child(newSlotId)
                    .get()
                    .addOnSuccessListener { slotSnapshot ->
                        val slot = slotSnapshot.getValue(com.example.medhomeapp.model.TimeSlot::class.java)

                        if (slot == null || slot.isBooked || !slot.isAvailable) {
                            callback(false, "Selected time slot is not available")
                            return@addOnSuccessListener
                        }

                        // Update appointment with new date and time
                        val updates = mapOf(
                            "date" to newDate,
                            "time" to newTime,
                            "updatedAt" to System.currentTimeMillis()
                        )

                        appointmentRef.child(appointmentId).updateChildren(updates)
                            .addOnSuccessListener {
                                // Free old slot
                                freeTimeSlot(appointment.doctorId, appointment.date, appointment.time)

                                // Book new slot
                                bookTimeSlot(appointment.doctorId, newDate, newSlotId, appointmentId)

                                callback(true, "Appointment rescheduled successfully")
                            }
                            .addOnFailureListener {
                                callback(false, "Failed to reschedule: ${it.message}")
                            }
                    }
            }
    }

    override fun completeAppointment(
        appointmentId: String,
        doctorNotes: String,
        callback: (Boolean, String) -> Unit
    ) {
        appointmentRef.child(appointmentId).get()
            .addOnSuccessListener { snapshot ->
                val appointment = snapshot.getValue(AppointmentModel::class.java)

                if (appointment == null) {
                    callback(false, "Appointment not found")
                    return@addOnSuccessListener
                }

                val updates = mapOf(
                    "status" to AppConstants.STATUS_COMPLETED,
                    "doctorNotes" to doctorNotes,
                    "completedAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                appointmentRef.child(appointmentId).updateChildren(updates)
                    .addOnSuccessListener {
                        // Move from upcoming to past
                        moveAppointmentIndex(
                            appointmentId,
                            appointment.patientId,
                            appointment.doctorId,
                            "upcoming",
                            "past"
                        )
                        callback(true, "Appointment marked as completed")
                    }
                    .addOnFailureListener {
                        callback(false, "Failed to complete appointment: ${it.message}")
                    }
            }
    }

    override fun markAsNoShow(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    ) {
        appointmentRef.child(appointmentId).get()
            .addOnSuccessListener { snapshot ->
                val appointment = snapshot.getValue(AppointmentModel::class.java)

                if (appointment == null) {
                    callback(false, "Appointment not found")
                    return@addOnSuccessListener
                }

                val updates = mapOf(
                    "status" to AppConstants.STATUS_NO_SHOW,
                    "updatedAt" to System.currentTimeMillis()
                )

                appointmentRef.child(appointmentId).updateChildren(updates)
                    .addOnSuccessListener {
                        // Move from upcoming to past
                        moveAppointmentIndex(
                            appointmentId,
                            appointment.patientId,
                            appointment.doctorId,
                            "upcoming",
                            "past"
                        )
                        callback(true, "Appointment marked as no-show")
                    }
                    .addOnFailureListener {
                        callback(false, "Failed to mark as no-show: ${it.message}")
                    }
            }
    }

    override fun updateAppointmentNotes(
        appointmentId: String,
        notes: String,
        isDoctor: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        val field = if (isDoctor) "doctorNotes" else "patientNotes"

        val updates = mapOf(
            field to notes,
            "updatedAt" to System.currentTimeMillis()
        )

        appointmentRef.child(appointmentId).updateChildren(updates)
            .addOnSuccessListener {
                callback(true, "Notes updated successfully")
            }
            .addOnFailureListener {
                callback(false, "Failed to update notes: ${it.message}")
            }
    }

    override fun canCancelAppointment(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    ) {
        appointmentRef.child(appointmentId).get()
            .addOnSuccessListener { snapshot ->
                val appointment = snapshot.getValue(AppointmentModel::class.java)

                if (appointment == null) {
                    callback(false, "Appointment not found")
                    return@addOnSuccessListener
                }

                // Check if already cancelled or completed
                if (appointment.status == AppConstants.STATUS_CANCELLED) {
                    callback(false, "Appointment is already cancelled")
                    return@addOnSuccessListener
                }

                if (appointment.status == AppConstants.STATUS_COMPLETED) {
                    callback(false, "Cannot cancel a completed appointment")
                    return@addOnSuccessListener
                }

                // Check if appointment is at least 2 hours away
                val appointmentTime = parseDateTime(appointment.date, appointment.time)
                val now = System.currentTimeMillis()
                val twoHoursInMillis = AppConstants.MIN_HOURS_BEFORE_CANCELLATION * 60 * 60 * 1000

                if ((appointmentTime - now) < twoHoursInMillis) {
                    callback(false, "Cannot cancel appointment less than ${AppConstants.MIN_HOURS_BEFORE_CANCELLATION} hours before scheduled time")
                    return@addOnSuccessListener
                }

                callback(true, "Cancellation allowed")
            }
            .addOnFailureListener {
                callback(false, "Failed to check cancellation eligibility: ${it.message}")
            }
    }

    // Helper functions
    private fun moveAppointmentIndex(
        appointmentId: String,
        patientId: String,
        doctorId: String,
        fromCategory: String,
        toCategory: String
    ) {
        // Remove from old category
        appointmentsByPatientRef.child(patientId).child(fromCategory).child(appointmentId).removeValue()
        appointmentsByDoctorRef.child(doctorId).child(fromCategory).child(appointmentId).removeValue()

        // Add to new category
        appointmentsByPatientRef.child(patientId).child(toCategory).child(appointmentId).setValue(true)
        appointmentsByDoctorRef.child(doctorId).child(toCategory).child(appointmentId).setValue(true)
    }

    private fun freeTimeSlot(doctorId: String, date: String, time: String) {
        // Find and update the slot
        availabilityRef.child(doctorId).child(date)
            .orderByChild("startTime")
            .equalTo(time)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.firstOrNull()?.ref?.updateChildren(
                        mapOf(
                            "isBooked" to false,
                            "appointmentId" to ""
                        )
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun bookTimeSlot(doctorId: String, date: String, slotId: String, appointmentId: String) {
        availabilityRef.child(doctorId).child(date).child(slotId)
            .updateChildren(
                mapOf(
                    "isBooked" to true,
                    "appointmentId" to appointmentId
                )
            )
    }

    private fun parseDateTime(date: String, time: String): Long {
        return try {
            val dateTime = "$date $time"
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                .parse(dateTime)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}