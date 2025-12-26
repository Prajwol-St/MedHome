package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _donorModel = MutableStateFlow<DonorModel?>(null)
    val donorProfile: StateFlow<DonorModel?> = _donorModel.asStateFlow()

    private val _donors = MutableStateFlow<List<DonorModel>>(emptyList())
    val donors: StateFlow<List<DonorModel>> = _donors.asStateFlow()

    fun getCurrentUserId(): String? = repository.getCurrentUserId()
}