package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot

interface DoctorAvailabilityRepo {

    fun addTimeSlot(slot: TimeSlot)

    fun deleteTimeSlot(
        doctorId: String,
        date: String,
        slotId: String
    )

    fun observeTimeSlots(
        doctorId: String,
        onResult: (List<TimeSlot>) -> Unit
    )

    fun getSlotsByDate(
        doctorId: String,
        date: String,
        onResult: (List<TimeSlot>) -> Unit
    )

    fun getAvailableSlots(
        doctorId: String,
        date: String,
        onResult: (List<TimeSlot>) -> Unit
    )

    fun updateSlotBookingStatus(
        doctorId: String,
        date: String,
        slotId: String,
        isBooked: Boolean,
        appointmentId: String
    )
}