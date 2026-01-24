package com.example.medhomeapp.repository

import android.content.Context
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.AppointmentNotificationIntegration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class AppointmentBookingRepoImpl(
    private val context: Context  // ADD THIS: Pass context for notifications
) : AppointmentBookingRepo {

    private val db = FirebaseDatabase.getInstance()
    private val availabilityRef = db.getReference("doctor_availability")
    private val appointmentRef = db.getReference("appointments")
    private val doctorRef = db.getReference("User")

    override fun bookAppointment(
        slot: TimeSlot,
        patient: UserModel,
        reason: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Path: doctor_availability/{doctorId}/{date}/{slotId}
        val slotRef = availabilityRef
            .child(slot.doctorId)
            .child(slot.date)
            .child(slot.id)

        slotRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentSlot = currentData.getValue(TimeSlot::class.java)

                // Check if slot is available and not booked
                if (currentSlot == null || currentSlot.isBooked || !currentSlot.isAvailable) {
                    return Transaction.abort()
                }

                // Generate appointment ID
                val appointmentId = appointmentRef.push().key ?: return Transaction.abort()

                // Mark slot as booked
                currentData.child("isBooked").value = true
                currentData.child("appointmentId").value = appointmentId

                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (!committed) {
                    callback(false, "Time slot already booked or unavailable")
                    return
                }

                val appointmentId = snapshot?.child("appointmentId")?.getValue(String::class.java)
                if (appointmentId == null) {
                    callback(false, "Failed to generate appointment ID")
                    return
                }

                // Fetch doctor details
                doctorRef.child(slot.doctorId).get()
                    .addOnSuccessListener { doctorSnapshot ->
                        val doctor = doctorSnapshot.getValue(DoctorModel::class.java)

                        if (doctor == null || doctor.role != "doctor") {
                            callback(false, "Doctor information not found")
                            return@addOnSuccessListener
                        }

                        // Create appointment with all details
                        val appointment = AppointmentModel(
                            appointmentId = appointmentId,
                            patientId = patient.id,
                            patientName = patient.name,
                            patientPhone = patient.contact,
                            doctorId = slot.doctorId,
                            doctorName = doctor.name,
                            specialization = doctor.specialization,
                            date = slot.date,
                            time = slot.startTime,
                            duration = slot.duration,
                            status = AppConstants.STATUS_PENDING,
                            appointmentType = AppConstants.TYPE_IN_PERSON,
                            consultationFee = doctor.consultationFee,
                            patientNotes = reason,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )

                        // Save appointment
                        appointmentRef.child(appointmentId)
                            .setValue(appointment.toMap())
                            .addOnSuccessListener {
                                // Create indexes for quick queries
                                createAppointmentIndexes(appointmentId, patient.id, slot.doctorId)

                                // ✅ Send booking confirmation and schedule reminders
                                AppointmentNotificationIntegration.onAppointmentBooked(
                                    context = context,
                                    appointment = appointment
                                ) { notifSuccess, notifMessage ->
                                    // Always return success for appointment booking
                                    callback(true, "Appointment booked successfully!")
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Rollback slot booking
                                slotRef.child("isBooked").setValue(false)
                                slotRef.child("appointmentId").setValue("")
                                callback(false, "Failed to save appointment: ${exception.message}")
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Rollback slot booking
                        slotRef.child("isBooked").setValue(false)
                        slotRef.child("appointmentId").setValue("")
                        callback(false, "Failed to fetch doctor details: ${exception.message}")
                    }
            }
        })
    }

    override fun getSlot(
        doctorId: String,
        date: String,
        slotId: String,
        callback: (TimeSlot?) -> Unit
    ) {
        availabilityRef
            .child(doctorId)
            .child(date)
            .child(slotId)
            .get()
            .addOnSuccessListener { snapshot ->
                val slot = snapshot.getValue(TimeSlot::class.java)
                callback(slot)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    override fun getAppointmentById(
        appointmentId: String,
        callback: (AppointmentModel?) -> Unit
    ) {
        appointmentRef.child(appointmentId)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointment = snapshot.getValue(AppointmentModel::class.java)
                callback(appointment)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Helper: Create indexes for efficient querying
    private fun createAppointmentIndexes(
        appointmentId: String,
        patientId: String,
        doctorId: String
    ) {
        // Index for patient's upcoming appointments
        db.getReference("appointmentsByPatient")
            .child(patientId)
            .child("upcoming")
            .child(appointmentId)
            .setValue(true)

        // Index for doctor's upcoming appointments
        db.getReference("appointmentsByDoctor")
            .child(doctorId)
            .child("upcoming")
            .child(appointmentId)
            .setValue(true)
    }
}