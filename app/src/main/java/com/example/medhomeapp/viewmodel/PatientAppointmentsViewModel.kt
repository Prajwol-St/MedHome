package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepo
import com.example.medhomeapp.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PatientAppointmentsViewModel(
    private val repo: AppointmentManagementRepo,
    private val patientId: String
) : ViewModel() {

    private val _upcomingAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val upcomingAppointments: StateFlow<List<AppointmentModel>> = _upcomingAppointments.asStateFlow()

    private val _pastAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val pastAppointments: StateFlow<List<AppointmentModel>> = _pastAppointments.asStateFlow()

    private val _cancelledAppointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val cancelledAppointments: StateFlow<List<AppointmentModel>> = _cancelledAppointments.asStateFlow()

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
        loadCancelledAppointments()
    }

    private fun loadUpcomingAppointments() {
        _isLoading.value = true
        repo.getUpcomingAppointments(patientId, AppConstants.ROLE_PATIENT) { appointments ->
            _upcomingAppointments.value = appointments
            _isLoading.value = false
        }
    }

    private fun loadPastAppointments() {
        repo.getPastAppointments(patientId, AppConstants.ROLE_PATIENT) { appointments ->
            _pastAppointments.value = appointments
        }
    }

    private fun loadCancelledAppointments() {
        repo.getCancelledAppointments(patientId, AppConstants.ROLE_PATIENT) { appointments ->
            _cancelledAppointments.value = appointments
        }
    }

    fun cancelAppointment(appointmentId: String, reason: String) {
        _isLoading.value = true
        repo.cancelAppointment(
            appointmentId,
            reason,
            AppConstants.CANCELLED_BY_PATIENT
        ) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun rescheduleAppointment(
        appointmentId: String,
        newDate: String,
        newTime: String,
        newSlotId: String
    ) {
        _isLoading.value = true
        repo.rescheduleAppointment(appointmentId, newDate, newTime, newSlotId) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun addPatientNotes(appointmentId: String, notes: String) {
        repo.updateAppointmentNotes(appointmentId, notes, false) { success, message ->
            _operationResult.value = success to message
            if (success) {
                loadAllAppointments()
            }
        }
    }

    fun canCancelAppointment(appointmentId: String, callback: (Boolean, String) -> Unit) {
        repo.canCancelAppointment(appointmentId) { allowed, message ->
            callback(allowed, message)
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }
}