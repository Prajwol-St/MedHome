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
        _isLoading.value = true
        repository.getAllBloodRequests(
            onSuccess = {requests ->
                _bloodRequests.value = requests
                _isLoading.value = false
                _error.value = null
            },
            onError = {exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to load blood requests"
            }
        )
    }

    fun getBloodRequestsByGroup(bloodGroup: String){
        _isLoading.value = true
        repository.getBloodRequestByGroup(
            bloodGroup = bloodGroup,
            onSuccess = {requests ->
                _bloodRequests.value = requests
                _isLoading.value = false
                _error.value = null
            },
            onError = {exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to load blood requests"
            }
        )
    }

    fun updateBloodRequestStatus(requestId: String, status: String){
        repository.updateBloodRequestStaus(
            requestId = requestId,
            status = status,
            onSuccess = {
                _successMessage.value = "Status updated successfully"
                getAllBloodRequests()
            },
            onError = {exception ->
                _error.value = exception.message ?: "Failed to update status"
            }
        )
    }

    fun deleteBloodRequest(requestId: String){
        _isLoading.value = true
        repository.deleteBloodRequest(
            requestId = requestId,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Blood request deleted successfully"
                getAllBloodRequests()
            },
            onError = {exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to delete blood request"
            }
        )
    }

    fun createOrUpdateDonorProfile(
        userName: String,
        bloodGroup: String,
        isAvailable: Boolean,
        isEmergencyAvailable: Boolean,
        contactNumber: String,
        location: String
    ){
        if (bloodGroup.isEmpty()){
            _error.value = "Please select your blood group"
            return
        }

        _isLoading.value = true
        val donorProfile = DonorModel(
            userName = userName,
            bloodGroup = bloodGroup,
            isAvailable = isAvailable,
            isEmergencyAvailable = isEmergencyAvailable,
            contactNumber = contactNumber,
            location = location
        )

        repository.createOrUpdateDonorProfile(
            donorProfile = donorProfile,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Donor profile saved successfully"
                _error.value = null
                loadDonorProfile()
            },
            onError = {exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to save donor profile"
            }
        )
    }

    fun loadDonorProfile(){
        val userId = getCurrentUserId() ?: return
        repository.getDonorProfile(
            userId = userId,
            onSuccess = {profile ->
                _donorModel.value = profile
            },
            onError = {exception ->
                _error.value = exception.message ?: "Failed to load donor profile"
            }
        )
    }

    fun getAllDonors(){
        repository.getAllDonors(
            onSuccess = {donorList ->
                _donors.value = donorList
            },
            onError = {exception ->
                _error.value = exception.message ?: "Failed to load donors"
            }
        )
    }

    fun getDonorsByBloodGroup(bloodGroup: String) {
        repository.getDonorsByBloodGroup(
            bloodGroup = bloodGroup,
            onSuccess = { donorsList ->
                _donors.value = donorsList
            },
            onError = { exception ->
                _error.value = exception.message ?: "Failed to load donors"
            }
        )
    }

    fun updateDonorAvailability(isAvailable: Boolean, isEmergencyAvailable: Boolean) {
        val userId = getCurrentUserId() ?: return
        repository.updateDonorAvailability(
            userId = userId,
            isAvailable = isAvailable,
            isEmergencyAvailable = isEmergencyAvailable,
            onSuccess = {
                _successMessage.value = "Availability updated successfully"
                // Reload the profile after updating
                loadDonorProfile()
            },
            onError = { exception ->
                _error.value = exception.message ?: "Failed to update availability"
            }
        )
    }

    fun getUserBloodRequests() {
        val userId = getCurrentUserId()
        if (userId == null) {
            _error.value = "User not authenticated"
            return
        }

        _isLoading.value = true
        repository.getAllBloodRequests(
            onSuccess = { requests ->
                // Filter requests by current user
                val userRequests = requests.filter { it.userId == userId }
                _bloodRequests.value = userRequests
                _isLoading.value = false
                _error.value = null
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to load your blood requests"
            }
        )
    }

    fun updateLastDonationDate(donationDate: Long) {
        val userId = getCurrentUserId() ?: return
        val currentProfile = _donorModel.value ?: return

        val updatedProfile = currentProfile.copy(lastDonationDate = donationDate)

        repository.createOrUpdateDonorProfile(
            donorProfile = updatedProfile,
            onSuccess = {
                _successMessage.value = "Donation date updated successfully"
                loadDonorProfile()
            },
            onError = { exception ->
                _error.value = exception.message ?: "Failed to update donation date"
            }
        )
    }

    fun markRequestAsFulfilled(requestId: String) {
        updateBloodRequestStatus(requestId, "fulfilled")
    }

    fun cancelBloodRequest(requestId: String) {
        updateBloodRequestStatus(requestId, "cancelled")
    }

    fun reactivateBloodRequest(requestId: String) {
        updateBloodRequestStatus(requestId, "active")
    }

    fun searchBloodRequestsByLocation(location: String) {
        val filteredRequests = _bloodRequests.value.filter {
            it.location.contains(location, ignoreCase = true) && it.status == "active"
        }
        _bloodRequests.value = filteredRequests
    }

    fun getUrgentBloodRequests() {
        val urgentRequests = _bloodRequests.value.filter {
            it.urgency == "Urgent" && it.status == "active"
        }
        _bloodRequests.value = urgentRequests
    }

    fun getCompatibleDonors(bloodGroup: String) {
        val compatibleGroups = when (bloodGroup) {
            "A+" -> listOf("A+", "A-", "O+", "O-")
            "A-" -> listOf("A-", "O-")
            "B+" -> listOf("B+", "B-", "O+", "O-")
            "B-" -> listOf("B-", "O-")
            "AB+" -> listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
            "AB-" -> listOf("A-", "B-", "AB-", "O-")
            "O+" -> listOf("O+", "O-")
            "O-" -> listOf("O-")
            else -> emptyList()
        }

        val compatibleDonors = _donors.value.filter {
            it.bloodGroup in compatibleGroups && it.isAvailable
        }
        _donors.value = compatibleDonors
    }

    fun clearError(){
        _error.value = null
    }

    fun clearSuccessMessage(){
        _successMessage.value = null
    }

    fun clearAllState(){
        _bloodRequests.value = emptyList()
        _donorModel.value = null
        _donors.value = emptyList()
        _error.value = null
        _successMessage.value = null
        _isLoading.value = false
    }

    fun refreshAllData(){
        getAllBloodRequests()
        loadDonorProfile()
        getAllDonors()
    }

}