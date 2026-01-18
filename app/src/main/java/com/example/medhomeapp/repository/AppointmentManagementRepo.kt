package com.example.medhomeapp.repository

import com.example.medhomeapp.model.AppointmentModel

interface AppointmentManagementRepo {

    fun getUpcomingAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    )

    fun getPastAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    )

    fun getCancelledAppointments(
        userId: String,
        role: String,
        callback: (List<AppointmentModel>) -> Unit
    )

    fun cancelAppointment(
        appointmentId: String,
        reason: String,
        cancelledBy: String,
        callback: (Boolean, String) -> Unit
    )

    fun rescheduleAppointment(
        appointmentId: String,
        newDate: String,
        newTime: String,
        newSlotId: String,
        callback: (Boolean, String) -> Unit
    )

    fun completeAppointment(
        appointmentId: String,
        doctorNotes: String,
        callback: (Boolean, String) -> Unit
    )

    fun markAsNoShow(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateAppointmentNotes(
        appointmentId: String,
        notes: String,
        isDoctor: Boolean,
        callback: (Boolean, String) -> Unit
    )

    fun canCancelAppointment(
        appointmentId: String,
        callback: (Boolean, String) -> Unit
    )
}