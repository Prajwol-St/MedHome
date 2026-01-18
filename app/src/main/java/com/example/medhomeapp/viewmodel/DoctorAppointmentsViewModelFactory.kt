package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.AppointmentManagementRepo

class DoctorAppointmentsViewModelFactory(
    private val repo: AppointmentManagementRepo,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorAppointmentsViewModel(repo, doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}