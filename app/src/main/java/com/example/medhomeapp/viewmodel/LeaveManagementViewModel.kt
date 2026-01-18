package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.DoctorLeaveModel
import com.example.medhomeapp.repository.LeaveManagementRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaveManagementViewModel(
    private val repo: LeaveManagementRepo,
    private val doctorId: String
) : ViewModel() {

    private val _leaves = MutableStateFlow<List<DoctorLeaveModel>>(emptyList())
    val leaves: StateFlow<List<DoctorLeaveModel>> = _leaves.asStateFlow()

    private val _activeLeaves = MutableStateFlow<List<DoctorLeaveModel>>(emptyList())
    val activeLeaves: StateFlow<List<DoctorLeaveModel>> = _activeLeaves.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _operationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val operationResult: StateFlow<Pair<Boolean, String>?> = _operationResult.asStateFlow()

    init {
        loadLeaves()
        loadActiveLeaves()
    }

    fun loadLeaves() {
        _isLoading.value = true
        repo.getLeaves(doctorId) { leaves ->
            _leaves.value = leaves
            _isLoading.value = false
        }
    }

    fun loadActiveLeaves() {
        repo.getActiveLeaves(doctorId) { leaves ->
            _activeLeaves.value = leaves
        }
    }

    fun addLeave(
        startDate: String,
        endDate: String,
        reason: String,
        leaveType: String
    ) {
        _isLoading.value = true

        val leave = DoctorLeaveModel(
            doctorId = doctorId,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            leaveType = leaveType,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )

        repo.addLeave(leave) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadLeaves()
                loadActiveLeaves()
            }
        }
    }

    fun deleteLeave(leaveId: String) {
        _isLoading.value = true
        repo.deleteLeave(doctorId, leaveId) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
            if (success) {
                loadLeaves()
                loadActiveLeaves()
            }
        }
    }

    fun isDoctorOnLeave(date: String, callback: (Boolean) -> Unit) {
        repo.isDoctorOnLeave(doctorId, date) { isOnLeave ->
            callback(isOnLeave)
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }
}