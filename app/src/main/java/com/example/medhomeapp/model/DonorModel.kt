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
    val lastDonationDate: Long = 0L,
    val timestamp: Long =  System.currentTimeMillis()
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "userId" to userId,
            "userName" to userName,
            "bloodGroup" to bloodGroup,
            "isAvailable" to isAvailable,
            "isEmergencyAvailable" to isEmergencyAvailable,
            "contactNumber" to contactNumber,
            "location" to location,
            "lastDonationDate" to lastDonationDate,
            "timestamp" to timestamp
        )
    }
}
