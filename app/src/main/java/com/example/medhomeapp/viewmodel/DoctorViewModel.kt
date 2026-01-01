package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorViewModel : ViewModel() {

    private val doctorRepo = DoctorRepoImpl()

    // ✅ ADD THIS
    private val _doctors = MutableStateFlow<List<DoctorModel>>(emptyList())
    val doctors = _doctors.asStateFlow()

    // ✅ ADD THIS
    fun loadDoctors() {
        doctorRepo.getAllDoctors { success, _, list ->
            if (success) {
                _doctors.value = list
            }
        }
    }

    fun addDoctor(
        doctor: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        doctorRepo.addDoctor(doctor, callback)
    }

    fun getDoctorByUserId(
        userId: String,
        callback: (Boolean, String, DoctorModel?) -> Unit
    ) {
        doctorRepo.getDoctorByUserId(userId, callback)
    }

    fun getAllDoctors(
        callback: (Boolean, String, List<DoctorModel>) -> Unit
    ) {
        doctorRepo.getAllDoctors(callback)
    }

    fun editDoctorProfile(
        userId: String,
        model: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        doctorRepo.editDoctorProfile(userId, model, callback)
    }

    fun deleteDoctor(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        doctorRepo.deleteDoctor(userId, callback)
    }

    fun addAvailability(date: String, time: String) { }

    fun addLeave(date: String) { }
}
