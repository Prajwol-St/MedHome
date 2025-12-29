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
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "id" to id,
            "donorId" to donorId,
            "donorName" to donorName,
            "bloodGroup" to bloodGroup,
            "recipientName" to recipientName,
            "hospital" to hospital,
            "location" to location,
            "unitsGiven" to unitsGiven,
            "donationDate" to donationDate,
            "notes" to notes
        )
    }
}
