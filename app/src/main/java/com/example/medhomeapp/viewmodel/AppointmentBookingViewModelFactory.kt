package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.AppointmentBookingRepo

class AppointmentBookingViewModelFactory(
    private val repo: AppointmentBookingRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentBookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentBookingViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
