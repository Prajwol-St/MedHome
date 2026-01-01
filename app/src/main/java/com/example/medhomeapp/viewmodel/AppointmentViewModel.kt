package com.example.medhomeapp.viewmodel

import AppointmentRepo
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.AppointmentModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppointmentViewModel(
    private val repository: AppointmentRepo
) : ViewModel() {

    /* ---------------- COMMON STATES ---------------- */

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    /* ---------------- ROLE (FIXED) ---------------- */

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun loadUserRole() {
        repository.getCurrentUserRole { role ->
            _userRole.value = role
        }
    }

    fun isDoctor(): Boolean = _userRole.value == "doctor"

    /* ---------------- DOCTOR APPOINTMENTS ---------------- */

    private val _doctorAppointments =
        MutableStateFlow<List<AppointmentModel>>(emptyList())
    val doctorAppointments: StateFlow<List<AppointmentModel>> =
        _doctorAppointments.asStateFlow()

    /* ---------------- USER ---------------- */

    fun getCurrentUserId(): String? = repository.getCurrentUserId()

    /* ---------------- ADD APPOINTMENT ---------------- */

    fun addAppointment(appointment: AppointmentModel) {
        _isLoading.value = true
        repository.addAppointment(appointment) { success, msg ->
            _isLoading.value = false
            if (success) {
                _success.value = msg
            } else {
                _error.value = msg
            }
        }
    }

    /* ---------------- LOAD DOCTOR APPOINTMENTS ---------------- */

    fun loadDoctorAppointments() {
        val doctorId = getCurrentUserId() ?: return

        _isLoading.value = true
        repository.getAppointmentsByDoctor(doctorId) { list, msg ->
            _isLoading.value = false
            if (list != null) {
                _doctorAppointments.value = list
            } else {
                _error.value = msg
            }
        }
    }

    /* ---------------- MARK DOCTOR LEAVE ---------------- */

    fun markDoctorLeave(dateMillis: Long) {
        val doctorId = getCurrentUserId() ?: return

        _isLoading.value = true
        repository.markDoctorLeave(doctorId, dateMillis) { success, msg ->
            _isLoading.value = false
            if (success) {
                _success.value = msg
            } else {
                _error.value = msg
            }
        }
    }

    /* ---------------- CLEAR STATES ---------------- */

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
}
