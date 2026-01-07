package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.DoctorRepoImpl

class DoctorViewModelFactory(
    private val repo: DoctorRepoImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
