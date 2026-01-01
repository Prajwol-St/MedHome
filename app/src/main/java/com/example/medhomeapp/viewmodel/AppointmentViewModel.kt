package com.example.medhomeapp.viewmodel

import AppointmentModel
import AppointmentRepo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medhomeapp.model.DoctorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val repository: AppointmentRepo
) : ViewModel() {

    /* ---------- LOADING / ERROR / SUCCESS ---------- */
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success = _success.asStateFlow()

    fun clearError() { _error.value = null }
    fun clearSuccess() { _success.value = null }

    /* ---------- USER ROLE ---------- */
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    fun loadUserRole() {
        repository.getCurrentUserRole { role ->
            _userRole.value = role
        }
    }

    fun isDoctor() = _userRole.value == "doctor"

    /* ---------- CURRENT USER ---------- */
    fun getCurrentUserId(): String? = repository.getCurrentUserId()

    /* ---------- DOCTORS ---------- */
    private val _doctors = MutableStateFlow<List<DoctorModel>>(emptyList())
    val doctors = _doctors.asStateFlow()

    fun loadDoctors() {
        _isLoading.value = true
        repository.getAllDoctors { list, msg ->
            _isLoading.value = false
            if (list != null) _doctors.value = list
            else _error.value = msg
        }
    }

    /* ---------- DOCTOR APPOINTMENTS ---------- */
    private val _doctorAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val doctorAppointments = _doctorAppointments.asStateFlow()

    fun loadDoctorAppointments() {
        val doctorId = getCurrentUserId() ?: return
        _isLoading.value = true
        repository.getAppointmentsByDoctor(doctorId) { list, msg ->
            _isLoading.value = false
            if (list != null) _doctorAppointments.value = list
            else _error.value = msg
        }
    }

    fun addAppointment(appointment: AppointmentModel) {
        _isLoading.value = true
        repository.addAppointment(appointment) { success, msg ->
            _isLoading.value = false
            if (success) _success.value = msg else _error.value = msg
        }
    }
}
