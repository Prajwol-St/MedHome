package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.HealthPackage
import com.example.medhomeapp.repository.HealthPackageRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HealthPackageViewModel(
    private val repository: HealthPackageRepo
) : ViewModel() {

    private val _packages = MutableStateFlow<List<HealthPackage>>(emptyList())
    val packages: StateFlow<List<HealthPackage>> = _packages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _selectedPackage = MutableStateFlow<HealthPackage?>(null)
    val selectedPackage: StateFlow<HealthPackage?> = _selectedPackage.asStateFlow()

    fun createPackage(
        name: String,
        description: String,
        testsIncluded: List<String>,
        price: Double,
        discountPercentage: Int,
        duration: String,
        recommendedFor: String
    ) {
        if (name.isEmpty() || description.isEmpty() || testsIncluded.isEmpty() ||
            price <= 0 || duration.isEmpty() || recommendedFor.isEmpty()
        ) {
            _error.value = "Please fill all required fields"
            return
        }

        _isLoading.value = true
        val healthPackage = HealthPackage(
            name = name,
            description = description,
            testsIncluded = testsIncluded,
            price = price,
            discountPercentage = discountPercentage,
            duration = duration,
            recommendedFor = recommendedFor,
            isActive = true
        )

        repository.createPackage(
            healthPackage = healthPackage,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Package created successfully"
                _error.value = null
                getAllPackages()
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to create package"
            }
        )
    }

    fun getAllPackages() {
        _isLoading.value = true
        repository.getAllPackages(
            onSuccess = { packagesList ->
                _packages.value = packagesList
                _isLoading.value = false
                _error.value = null
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to load packages"
            }
        )
    }

    fun getActivePackages() {
        _isLoading.value = true
        repository.getActivePackages(
            onSuccess = { packagesList ->
                _packages.value = packagesList
                _isLoading.value = false
                _error.value = null
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to load packages"
            }
        )
    }

    fun getPackageById(packageId: String) {
        repository.getPackageById(
            packageId = packageId,
            onSuccess = { healthPackage ->
                _selectedPackage.value = healthPackage
            },
            onError = { exception ->
                _error.value = exception.message ?: "Failed to load package details"
            }
        )
    }

    fun updatePackage(
        packageId: String,
        name: String,
        description: String,
        testsIncluded: List<String>,
        price: Double,
        discountPercentage: Int,
        duration: String,
        recommendedFor: String,
        isActive: Boolean
    ) {
        if (name.isEmpty() || description.isEmpty() || testsIncluded.isEmpty() ||
            price <= 0 || duration.isEmpty() || recommendedFor.isEmpty()
        ) {
            _error.value = "Please fill all required fields"
            return
        }

        _isLoading.value = true
        val healthPackage = HealthPackage(
            id = packageId,
            name = name,
            description = description,
            testsIncluded = testsIncluded,
            price = price,
            discountPercentage = discountPercentage,
            duration = duration,
            recommendedFor = recommendedFor,
            isActive = isActive
        )

        repository.updatePackage(
            packageId = packageId,
            healthPackage = healthPackage,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Package updated successfully"
                _error.value = null
                getAllPackages()
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to update package"
            }
        )
    }

    fun deletePackage(packageId: String) {
        _isLoading.value = true
        repository.deletePackage(
            packageId = packageId,
            onSuccess = {
                _isLoading.value = false
                _successMessage.value = "Package deleted successfully"
                getAllPackages()
            },
            onError = { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Failed to delete package"
            }
        )
    }

    fun togglePackageStatus(packageId: String, currentStatus: Boolean) {
        repository.updatePackageStatus(
            packageId = packageId,
            isActive = !currentStatus,
            onSuccess = {
                _successMessage.value = if (!currentStatus) "Package activated" else "Package deactivated"
                getAllPackages()
            },
            onError = { exception ->
                _error.value = exception.message ?: "Failed to update package status"
            }
        )
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearSelectedPackage() {
        _selectedPackage.value = null
    }

    fun clearAllState() {
        _packages.value = emptyList()
        _selectedPackage.value = null
        _error.value = null
        _successMessage.value = null
        _isLoading.value = false
    }
}