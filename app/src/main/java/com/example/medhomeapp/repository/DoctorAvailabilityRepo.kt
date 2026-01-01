package com.example.medhomeapp.repository

interface DoctorAvailabilityRepo {
    fun addAvailability(
        doctorId: String,
        date: String,
        time: String,
        callback: (Boolean, String) -> Unit
    )

    fun getAvailability(
        doctorId: String,
        date: String,
        callback: (List<String>) -> Unit
    )

    fun removeAvailability(
        doctorId: String,
        date: String,
        time: String,
        callback: (Boolean, String) -> Unit
    )
}