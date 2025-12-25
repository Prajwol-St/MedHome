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

    fun canDonate(): Boolean {
        if (lastDonationDate == 0L) return true
        val threeMonthsInMillis = 90L * 24 * 60 * 60 * 1000 //90 days
        return (System.currentTimeMillis() - lastDonationDate) >= threeMonthsInMillis
    }

    fun daysSinceLastDonation(): Int {
        if (lastDonationDate == 0L) return -1
        val diff = System.currentTimeMillis() - lastDonationDate
        return (diff / (24*60*60*1000)).toInt()
    }

    fun daysUntilNextDonation(): Int {
        if (lastDonationDate == 0L) return 0
        val daysSince = daysSinceLastDonation()
        val daysUntil = 90 - daysSince
        return if (daysUntil < 0) 0 else daysUntil
    }
}
