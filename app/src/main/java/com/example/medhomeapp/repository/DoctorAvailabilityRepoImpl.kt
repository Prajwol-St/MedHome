package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot
import com.google.firebase.database.*

class DoctorAvailabilityRepoImpl : DoctorAvailabilityRepo {

    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("doctor_availability")

    override fun addTimeSlot(slot: TimeSlot) {
        val key = dbRef.push().key ?: return
        dbRef.child(key).setValue(slot.copy(id = key))
    }

    override fun deleteTimeSlot(id: String) {
        dbRef.child(id).removeValue()
    }

    override fun observeTimeSlots(doctorId: String, onResult: (List<TimeSlot>) -> Unit) {
        dbRef.orderByChild("doctorId").equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<TimeSlot>()
                    snapshot.children.forEach {
                        it.getValue(TimeSlot::class.java)?.let { slot ->
                            list.add(slot)
                        }
                    }
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }
}