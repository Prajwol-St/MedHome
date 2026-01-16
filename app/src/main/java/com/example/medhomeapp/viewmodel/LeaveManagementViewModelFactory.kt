package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medhomeapp.repository.LeaveManagementRepo

class LeaveManagementViewModelFactory(
    private val repo: LeaveManagementRepo,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaveManagementViewModel(repo, doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}