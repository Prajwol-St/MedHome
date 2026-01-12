package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot
import com.google.firebase.database.*

class DoctorAvailabilityRepoImpl : DoctorAvailabilityRepo {

    private val rootRef =
        FirebaseDatabase.getInstance().getReference("doctor_availability")

    override fun addTimeSlot(slot: TimeSlot) {
        val doctorRef = rootRef.child(slot.doctorId)
        val key = doctorRef.push().key ?: return
        doctorRef.child(key).setValue(slot.copy(id = key))
    }

    override fun deleteTimeSlot(doctorId: String, slotId: String) {
        rootRef.child(doctorId).child(slotId).removeValue()
    }

    override fun observeTimeSlots(
        doctorId: String,
        onResult: (List<TimeSlot>) -> Unit
    ) {
        rootRef.child(doctorId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val slots = snapshot.children.mapNotNull { child ->
                        val slot = child.getValue(TimeSlot::class.java)
                        slot?.copy(id = child.key ?: "")
                    }
                    onResult(slots)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

}
