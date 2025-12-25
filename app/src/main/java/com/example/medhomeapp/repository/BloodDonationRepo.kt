package com.example.medhomeapp.repository

interface BloodDonationRepo {
    fun postBloodRequest(
        bloodRequest: BloodRequest,
        onSuccess: () -> Unit
    )

}