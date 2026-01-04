package com.example.medhomeapp.repository

import com.example.medhomeapp.model.HealthPackage

interface HealthPackageRepo {
    fun createPackage(
        healthPackage: HealthPackage,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getAllPackages(
        onSuccess: (List<HealthPackage>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getActivePackages(
        onSuccess: (List<HealthPackage>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getPackageById(
        packageId: String,
        onSuccess: (HealthPackage?) -> Unit,
        onError: (Exception) -> Unit
    )

    fun updatePackage(
        packageId: String,
        healthPackage: HealthPackage,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun deletePackage(
        packageId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun updatePackageStatus(
        packageId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
}