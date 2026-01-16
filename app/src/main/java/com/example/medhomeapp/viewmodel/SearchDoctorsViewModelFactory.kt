package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.DoctorRepo

class SearchDoctorsViewModelFactory(
    private val repo: DoctorRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchDoctorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchDoctorsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}