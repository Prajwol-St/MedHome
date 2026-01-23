package com.example.medhomeapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.AppointmentManagementRepoImpl

class DoctorAppointmentsViewModelFactory(
    private val context: Context,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorAppointmentsViewModel(
                repo = AppointmentManagementRepoImpl(context),
                doctorId = doctorId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}