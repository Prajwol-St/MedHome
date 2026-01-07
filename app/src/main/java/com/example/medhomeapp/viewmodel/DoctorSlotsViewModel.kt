package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorSlotsViewModel(
    private val repo: DoctorAvailabilityRepo = DoctorAvailabilityRepoImpl()
) : ViewModel() {

    private val _slots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val slots = _slots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun observeSlots(doctorId: String) {
        _isLoading.value = true
        _slots.value = emptyList()

        repo.observeTimeSlots(doctorId) { list ->
            _slots.value = list
            _isLoading.value = false
        }
    }
}
