package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DoctorAvailabilityViewModel(
    private val repo: DoctorAvailabilityRepo,
    private val doctorId: String
) : ViewModel() {

    private val _timeSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val timeSlots: StateFlow<List<TimeSlot>> = _timeSlots

    init {
        repo.observeTimeSlots(doctorId) {
            _timeSlots.value = it
        }
    }

    fun addSlot(day: String, start: String, end: String) {
        val slot = TimeSlot(
            doctorId = doctorId,
            day = day,
            startTime = start,
            endTime = end
        )
        repo.addTimeSlot(slot)
    }

    fun deleteSlot(id: String) {
        repo.deleteTimeSlot(id)
    }
}

