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

}