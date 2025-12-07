package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorModel

class DoctorRepoImpl: DoctorRepo {
    override fun addDoctor(
        doctor: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getDoctorByUserId(
        userId: String,
        callback: (Boolean, String, DoctorModel?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAllDoctors(callback: (Boolean, String, List<DoctorModel>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun editDoctorProfile(
        userId: String,
        model: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteDoctor(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}