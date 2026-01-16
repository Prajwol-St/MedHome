package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepo
import com.example.medhomeapp.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorAppointmentsViewModel(
    private val repo: AppointmentManagementRepo,
    private val doctorId: String
) : ViewModel() {

    private val _upcomingAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val upcomingAppointments: StateFlow<List<AppointmentModel>> = _upcomingAppointments.asStateFlow()

    private val _todayAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val todayAppointments: StateFlow<List<AppointmentModel>> = _todayAppointments.asStateFlow()

    private val _pastAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val pastAppointments: StateFlow<List<AppointmentModel>> = _pastAppointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _operationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val operationResult: StateFlow<Pair<Boolean, String>?> = _operationResult.asStateFlow()

    init {
        loadAllAppointments()
    }

    fun loadAllAppointments() {
        loadUpcomingAppointments()
        loadPastAppointments()
    }

    private fun loadUpcomingAppointments() {
        _isLoading.value = true
        repo.getUpcomingAppointments(doctorId, AppConstants.ROLE_DOCTOR) { appointments ->
            _upcomingAppointments.value = appointments

            // Filter today's appointments
            val today = getCurrentDate()
            _todayAppointments.value = appointments.filter { it.date == today }

            _isLoading.value = false
        }
    }

    private fun loadPastAppointments() {
        repo.getPastAppointments(doctorId, AppConstants.ROLE_DOCTOR) { appointments ->
            _pastAppointments.value = appointments
        }
    }

    fun completeAppointment(appointmentId: String, doctorNotes: String) {
        _isLoading.value = true
        repo.completeAppointment(appointmentId, doctorNotes) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun markAsNoShow(appointmentId: String) {
        _isLoading.value = true
        repo.markAsNoShow(appointmentId) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun addDoctorNotes(appointmentId: String, notes: String) {
        repo.updateAppointmentNotes(appointmentId, notes, true) { success, message ->
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun cancelAppointment(appointmentId: String, reason: String) {
        _isLoading.value = true
        repo.cancelAppointment(
            appointmentId,
            reason,
            AppConstants.CANCELLED_BY_DOCTOR
        ) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}