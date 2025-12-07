package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorModel

interface DoctorRepo {
    fun addDoctor(
        doctor: DoctorModel,
        callback: (Boolean, String) -> Unit
    )

    fun getDoctorByUserId(
        userId: String,
        callback: (Boolean, String, DoctorModel?) -> Unit
    )

    fun getAllDoctors(
        callback: (Boolean, String, List<DoctorModel>) -> Unit
    )

    fun editDoctorProfile(
        userId: String,
        model: DoctorModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteDoctor(
        userId: String,
        callback: (Boolean, String) -> Unit
    )
}