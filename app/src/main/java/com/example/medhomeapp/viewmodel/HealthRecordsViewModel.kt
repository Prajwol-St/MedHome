package com.example.medhomeapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.medhomeapp.model.HealthRecordsModel
import com.example.medhomeapp.repository.HealthRecordsRepo
import com.example.medhomeapp.repository.HealthRecordsRepoImpl

class HealthRecordsViewModel(application: Application): AndroidViewModel(application) {

    private val repository: HealthRecordsRepo = HealthRecordsRepoImpl(application.applicationContext)

    private val _healthRecords = MutableLiveData<List<HealthRecordsModel>>()
    val healthRecords: LiveData<List<HealthRecordsModel>> = _healthRecords

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    init {
        observeHealthRecords()
    }

    private fun observeHealthRecords() {
        _isLoading.value = true
        repository.getHealthRecords(
            onSuccess = { records ->
                _healthRecords.value = records
                _isLoading.value = false
            },
            onError = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
        )
    }

    fun addHealthRecord(record: HealthRecordsModel, fileUri: Uri?) {
        _isLoading.value = true
        repository.addHealthRecord(
            record = record,
            fileUri = fileUri,
            onSuccess = {
                _successMessage.value = "Record added successfully"
                _isLoading.value = false
            },
            onError = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
        )
    }

    fun updateHealthRecord(record: HealthRecordsModel, fileUri: Uri?) {
        _isLoading.value = true
        repository.updateHealthRecord(
            record = record,
            fileUri = fileUri,
            onSuccess = {
                _successMessage.value = "Record updated successfully"
                _isLoading.value = false
            },
            onError = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
        )
    }

    fun deleteHealthRecord(recordId: String, fileUrl: String) {
        _isLoading.value = true
        repository.deleteHealthRecord(
            recordId = recordId,
            fileUrl = fileUrl,
            onSuccess = {
                _successMessage.value = "Record deleted successfully"
                _isLoading.value = false
            },
            onError = { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
        )
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}