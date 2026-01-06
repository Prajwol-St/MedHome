package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DoctorSlotsViewModel(
    private val repo: DoctorAvailabilityRepo
) : ViewModel() {

    private val _slots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val slots: StateFlow<List<TimeSlot>> = _slots

    fun observeSlots(doctorId: String) {
        repo.observeTimeSlots(doctorId) { list ->
            _slots.value = list.filter { it.isAvailable }
        }
    }
}
