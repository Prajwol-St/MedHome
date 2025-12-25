package com.example.medhomeapp.repository

import com.example.medhomeapp.model.BloodRequestModel

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

}