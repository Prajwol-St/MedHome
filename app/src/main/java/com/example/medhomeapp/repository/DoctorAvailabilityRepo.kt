package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot

interface DoctorAvailabilityRepo {
    fun addTimeSlot(slot: TimeSlot)
    fun deleteTimeSlot(id: String)
    fun observeTimeSlots(doctorId: String, onResult: (List<TimeSlot>) -> Unit)
}