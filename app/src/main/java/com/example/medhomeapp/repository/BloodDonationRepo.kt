package com.example.medhomeapp.repository

import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel

interface BloodDonationRepo {
    fun postBloodRequest(
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun getAllBloodRequests(
        onSuccess: (List<BloodRequestModel>)-> Unit,
        onError: (Exception) -> Unit
    )
    fun getBloodRequestByGroup(
        bloodGroup: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
    )
    fun getBloodRequestsByUserId(
        userId: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    )
    fun getBloodRequestById(
        requestId: String,
        onSuccess: (BloodRequestModel) -> Unit,
        onError: (Exception) -> Unit
    )
    fun updateBloodRequest(
        requestId: String,
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun updateBloodRequestStaus(
        requestId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun deleteBloodRequest(
        requestId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun createOrUpdateDonorProfile(
        donorProfile: DonorModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun getDonorProfile(
        userId: String,
        onSuccess: (DonorModel?) -> Unit,
        onError: (Exception) -> Unit
    )
    fun getAllDonors(
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    )
    fun getDonorsByBloodGroup(
        bloodGroup: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    )
    fun updateDonorAvailability(
        userId: String,
        isAvailable: Boolean,
        isEmergencyAvailable: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun updateLastDonationDate(
        userId: String,
        donationDate: Long,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun deleteDonorProfile(
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun searchBloodRequestByLocation(
        location: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    )
    fun getUrgentBloodRequests(
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    )
    fun getAvailableDonorsByLocation(
        location: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getCurrentUserId(): String?

}