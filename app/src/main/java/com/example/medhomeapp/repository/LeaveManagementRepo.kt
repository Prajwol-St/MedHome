package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorLeaveModel

interface LeaveManagementRepo {

    fun addLeave(
        leave: DoctorLeaveModel,
        callback: (Boolean, String) -> Unit
    )

    fun getLeaves(
        doctorId: String,
        callback: (List<DoctorLeaveModel>) -> Unit
    )

    fun deleteLeave(
        doctorId: String,
        leaveId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getActiveLeaves(
        doctorId: String,
        callback: (List<DoctorLeaveModel>) -> Unit
    )

    fun isDoctorOnLeave(
        doctorId: String,
        date: String,
        callback: (Boolean) -> Unit
    )
}