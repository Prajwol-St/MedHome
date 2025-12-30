package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentRepo
import com.example.medhomeapp.repository.AppointmentRepoImpl
import com.example.medhomeapp.repository.BloodDonationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppointmentViewModel(
    private val repository: AppointmentRepo
) : ViewModel() {

    private val appointmentRepo = AppointmentRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _appointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val appointments: StateFlow<List<AppointmentModel>> = _appointments.asStateFlow()



    fun addAppointment(appointment: AppointmentModel) {
        _isLoading.value = true

        appointmentRepo.addAppointment(appointment) { success, message ->
            _isLoading.value = false
            if (success) {
                _successMessage.value = message
            } else {
                _error.value = message
            }
        }
    }



    fun updateAppointment(appointmentId: String, appointment: AppointmentModel) {
        _isLoading.value = true

        appointmentRepo.updateAppointment(appointmentId, appointment) { success, message ->
            _isLoading.value = false
            if (success) {
                _successMessage.value = message
            } else {
                _error.value = message
            }
        }
    }




    fun deleteAppointment(appointmentId: String) {
        _isLoading.value = true

        appointmentRepo.deleteAppointment(appointmentId) { success, message ->
            _isLoading.value = false
            if (success) {
                _successMessage.value = message
            } else {
                _error.value = message
            }
        }
    }




    fun getAppointmentsByPatientId(patientId: String) {
        _isLoading.value = true

        appointmentRepo.getAppointmentsByPatientId(patientId) { success, message, list ->
            _isLoading.value = false
            if (success) {
                _appointments.value = list
            } else {
                _error.value = message
            }
        }
    }




    fun getAppointmentsByDoctorId(doctorId: String) {
        _isLoading.value = true

        appointmentRepo.getAppointmentsByDoctorId(doctorId) { success, message, list ->
            _isLoading.value = false
            if (success) {
                _appointments.value = list
            } else {
                _error.value = message
            }
        }
    }



    fun getCurrentUserId(): String? {
        return appointmentRepo.getCurrentUserId()
    }


    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }




}