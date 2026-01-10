package com.example.medhomeapp.repository

import com.example.medhomeapp.model.HealthPackageModel

interface HealthPackageRepo {

    fun createPackage(
        packageModel: HealthPackageModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAllPackages(
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    )

    fun getPackagesByDoctor(
        doctorId: String,
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    )

    fun getActivePackages(
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    )

    fun getPackageById(
        packageId: String,
        callback: (Boolean, String, HealthPackageModel?) -> Unit
    )

    fun updatePackage(
        packageId: String,
        packageModel: HealthPackageModel,
        callback: (Boolean, String) -> Unit
    )

    fun deletePackage(
        packageId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getPackagesByCategory(
        category: String,
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    )
}