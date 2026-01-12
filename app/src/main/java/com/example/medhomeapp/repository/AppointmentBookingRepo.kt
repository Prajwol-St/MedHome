package com.example.medhomeapp.repository

import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel

interface AppointmentBookingRepo {
    fun bookAppointment(
        slot: TimeSlot,
        patient: UserModel,
        reason: String,
        callback: (Boolean, String) -> Unit
    )

    fun getSlot(
        doctorId: String,
        slotId: String,
        callback: (TimeSlot?) -> Unit
    )
}