package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorViewModel(
    private val doctorRepo: DoctorRepoImpl
) : ViewModel() {

    private val _doctors = MutableStateFlow<List<DoctorModel>>(emptyList())
    val doctors = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadDoctors() {
        _isLoading.value = true
        doctorRepo.getAllDoctors { success, _, list ->
            if (success) {
                _doctors.value = list
            }
            _isLoading.value = false
        }
    }
}

