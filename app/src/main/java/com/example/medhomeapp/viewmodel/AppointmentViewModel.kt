package com.example.medhomeapp.viewmodel

import AppointmentModel
import AppointmentRepo
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppointmentViewModel(
    private val repository: AppointmentRepo
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success = _success.asStateFlow()

    /* ---------- ROLE ---------- */

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    fun loadUserRole() {
        repository.getCurrentUserRole {
            _userRole.value = it
        }
    }

    fun isDoctor() = _userRole.value == "doctor"

    /* ---------- APPOINTMENTS ---------- */

    private val _doctorAppointments =
        MutableStateFlow<List<AppointmentModel>>(emptyList())
    val doctorAppointments = _doctorAppointments.asStateFlow()

    fun getCurrentUserId(): String? = repository.getCurrentUserId()

    fun addAppointment(appointment: AppointmentModel) {
        _isLoading.value = true
        repository.addAppointment(appointment) { success, msg ->
            _isLoading.value = false
            if (success) _success.value = msg else _error.value = msg
        }
    }

    fun loadDoctorAppointments() {
        val doctorId = getCurrentUserId() ?: return // Firebase UID
        _isLoading.value = true
        repository.getAppointmentsByDoctor(doctorId) { list, msg ->
            _isLoading.value = false
            if (list != null) _doctorAppointments.value = list
            else _error.value = msg
        }
    }


    fun clearError() { _error.value = null }
    fun clearSuccess() { _success.value = null }
}