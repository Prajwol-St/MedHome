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

    fun postBloodRequest(
        patientName: String,
        bloodGroup: String,
        unitsNeeded: String,
        hospital: String,
        location: String,
        contactNumber: String,
        urgencyLevel: String,
        additionalNotes: String
    ) {
        if (bloodGroup.isEmpty() || unitsNeeded.isEmpty() || hospital.isEmpty() ||
            location.isEmpty() || contactNumber.isEmpty() || urgencyLevel.isEmpty()
        ) {
            _error.value = "Please fill all required fields"
            return
        }
        _isLoading.value = true
        val bloodRequest = BloodRequestModel(
            patientName = patientName.ifEmpty { "Anonymous" },
            bloodGroup = bloodGroup,
            unitsNeeded = unitsNeeded,
            hospital = hospital,
            location = location,
            contactNumber = contactNumber,
            urgency = urgencyLevel,
            additionalNotes = additionalNotes
        )

        repository.postBloodRequest(
            bloodRequest = bloodRequest,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Blood request posted successfully"
                _error.value = null

                getAllBloodRequests()
            },
            onError = {exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to post blood request"
            }
        )
    }

    fun getAllBloodRequests(){

    }
}