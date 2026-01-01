package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.repository.DoctorLeaveRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorLeaveViewModel(
    private val repo: DoctorLeaveRepo
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success = _success.asStateFlow()

    fun addLeave(doctorId: String, date: String) {
        _isLoading.value = true
        repo.addLeave(doctorId, date) { success, msg ->
            _isLoading.value = false
            if (success) _success.value = msg else _error.value = msg
        }
    }
}
