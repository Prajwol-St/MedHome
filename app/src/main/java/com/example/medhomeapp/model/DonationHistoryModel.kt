package com.example.medhomeapp.model

data class DonationHistoryModel(
    val id: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val bloodGroup: String = "",
    val recipientName: String = "",
    val hospital: String = "",
    val location: String = "",
    val unitsGiven: String = "",
    val donationDate: Long = System.currentTimeMillis(),
    val notes: String =""
)
