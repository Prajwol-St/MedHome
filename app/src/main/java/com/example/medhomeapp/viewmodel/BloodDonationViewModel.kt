package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.repository.BloodDonationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BloodDonationViewModel(
    private val repository: BloodDonationRepo
) : ViewModel() {

    private val _bloodRequests = MutableStateFlow<List<BloodRequestModel>>(emptyList())
    val bloodRequests: StateFlow<List<BloodRequestModel>> = _bloodRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
}