package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot
import com.google.firebase.database.*

class DoctorAvailabilityRepoImpl : DoctorAvailabilityRepo {

    private val rootRef = FirebaseDatabase.getInstance()
        .getReference("doctor_availability")

    override fun addTimeSlot(slot: TimeSlot) {
        // Path: doctor_availability/{doctorId}/{date}/{slotId}
        val doctorRef = rootRef.child(slot.doctorId).child(slot.date)
        val key = doctorRef.push().key ?: return
        doctorRef.child(key).setValue(slot.copy(id = key))
    }

    override fun deleteTimeSlot(doctorId: String, date: String, slotId: String) {
        rootRef.child(doctorId)
            .child(date)
            .child(slotId)
            .removeValue()
    }

    override fun observeTimeSlots(
        doctorId: String,
        onResult: (List<TimeSlot>) -> Unit
    ) {
        rootRef.child(doctorId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val slots = mutableListOf<TimeSlot>()

                    // Iterate through dates
                    for (dateSnapshot in snapshot.children) {
                        // Iterate through slots in each date
                        for (slotSnapshot in dateSnapshot.children) {
                            val slot = slotSnapshot.getValue(TimeSlot::class.java)
                            if (slot != null) {
                                slots.add(slot.copy(id = slotSnapshot.key ?: ""))
                            }
                        }
                    }
                    onResult(slots)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    override fun getSlotsByDate(
        doctorId: String,
        date: String,
        onResult: (List<TimeSlot>) -> Unit
    ) {
        rootRef.child(doctorId)
            .child(date)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val slots = snapshot.children.mapNotNull { child ->
                        child.getValue(TimeSlot::class.java)?.copy(id = child.key ?: "")
                    }
                    onResult(slots)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    override fun getAvailableSlots(
        doctorId: String,
        date: String,
        onResult: (List<TimeSlot>) -> Unit
    ) {
        rootRef.child(doctorId)
            .child(date)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val slots = snapshot.children.mapNotNull { child ->
                        val slot = child.getValue(TimeSlot::class.java)
                        // Only return slots that are available AND not booked
                        if (slot != null && slot.isAvailable && !slot.isBooked) {
                            slot.copy(id = child.key ?: "")
                        } else {
                            null
                        }
                    }
                    onResult(slots)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    override fun updateSlotBookingStatus(
        doctorId: String,
        date: String,
        slotId: String,
        isBooked: Boolean,
        appointmentId: String
    ) {
        val slotRef = rootRef.child(doctorId).child(date).child(slotId)

        val updates = mapOf(
            "isBooked" to isBooked,
            "appointmentId" to appointmentId
        )

        slotRef.updateChildren(updates)
    }
}