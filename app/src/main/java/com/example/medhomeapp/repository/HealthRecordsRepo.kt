package com.example.medhomeapp.repository

import android.net.Uri
import com.example.medhomeapp.model.HealthRecordsModel

interface HealthRecordsRepo {
    fun addHealthRecord(
        record: HealthRecordsModel,
        fileUri: Uri?,
        onSuccess: ()-> Unit,
        onError:(Exception)-> Unit
    )
    fun getHealthRecords(
        onSuccess: (List<HealthRecordsModel>)-> Unit,
        onError: (Exception) -> Unit
    )
    fun updateHealthRecord(
        record: HealthRecordsModel,
        fileUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
    fun deleteHealthRecord(
        recordId: String,
        fileUri: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
}