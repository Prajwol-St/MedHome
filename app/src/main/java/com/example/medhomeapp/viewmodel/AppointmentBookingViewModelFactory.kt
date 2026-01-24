package com.example.medhomeapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.AppointmentBookingRepoImpl

class AppointmentBookingViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentBookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentBookingViewModel(
                AppointmentBookingRepoImpl(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}