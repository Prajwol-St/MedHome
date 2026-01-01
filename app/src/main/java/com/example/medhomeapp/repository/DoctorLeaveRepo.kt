package com.example.medhomeapp.repository

interface DoctorLeaveRepo {
    fun addLeave(
        doctorId: String,
        date: String,
        callback: (Boolean, String) -> Unit
    )

    fun isDoctorOnLeave(
        doctorId: String,
        date: String,
        callback: (Boolean) -> Unit
    )
}