package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class DoctorAvailabilityViewModel(
    private val repo: DoctorAvailabilityRepo,
    private val doctorId: String
) : ViewModel() {

    // All slots (all dates)
    private val _allSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val allSlots: StateFlow<List<TimeSlot>> = _allSlots

    // Slots for selected date
    private val _slotsByDate = MutableStateFlow<List<TimeSlot>>(emptyList())
    val slotsByDate: StateFlow<List<TimeSlot>> = _slotsByDate

    // Available slots for booking
    private val _availableSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val availableSlots: StateFlow<List<TimeSlot>> = _availableSlots

    init {
        observeAllSlots()
    }

    private fun observeAllSlots() {
        repo.observeTimeSlots(doctorId) { slots ->
            _allSlots.value = slots
        }
    }

    fun loadSlotsByDate(date: String) {
        repo.getSlotsByDate(doctorId, date) { slots ->
            _slotsByDate.value = slots
        }
    }

    fun loadAvailableSlots(date: String) {
        repo.getAvailableSlots(doctorId, date) { slots ->
            _availableSlots.value = slots
        }
    }

    fun addSlot(
        date: String,
        startTime: String,
        endTime: String
    ) {
        val slot = TimeSlot(
            id = UUID.randomUUID().toString(), // overwritten by repo push()
            doctorId = doctorId,
            date = date,
            startTime = startTime,
            endTime = endTime,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        repo.addTimeSlot(slot)
    }

    fun deleteSlot(date: String, slotId: String) {
        repo.deleteTimeSlot(doctorId, date, slotId)
    }

    fun markSlotBooked(
        date: String,
        slotId: String,
        appointmentId: String
    ) {
        repo.updateSlotBookingStatus(
            doctorId = doctorId,
            date = date,
            slotId = slotId,
            isBooked = true,
            appointmentId = appointmentId
        )
    }

    fun markSlotAvailable(date: String, slotId: String) {
        repo.updateSlotBookingStatus(
            doctorId = doctorId,
            date = date,
            slotId = slotId,
            isBooked = false,
            appointmentId = ""
        )
    }
}
