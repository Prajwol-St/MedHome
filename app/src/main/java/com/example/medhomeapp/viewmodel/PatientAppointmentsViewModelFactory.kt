package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.AppointmentManagementRepo

class PatientAppointmentsViewModelFactory(
    private val repo: AppointmentManagementRepo,
    private val patientId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientAppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientAppointmentsViewModel(repo, patientId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}