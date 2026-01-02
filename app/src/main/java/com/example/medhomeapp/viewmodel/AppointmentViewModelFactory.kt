package com.example.medhomeapp.viewmodel

import AppointmentRepo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.DoctorRepo

class AppointmentViewModelFactory(
    private val appointmentRepo: AppointmentRepo,
    private val doctorRepo: DoctorRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(
                appointmentRepo,
                doctorRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
