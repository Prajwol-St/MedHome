package com.example.medhomeapp.model

data class DonorModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val bloodGroup: String = "",
    val isAvailable: Boolean =  false,
    val isEmergencyAvailable: Boolean = false,
    val contactNumber: String ="",
    val location: String = "",
    val lastDonationDate: Long? = null,
    val timestamp: Long =  System.currentTimeMillis()
)
