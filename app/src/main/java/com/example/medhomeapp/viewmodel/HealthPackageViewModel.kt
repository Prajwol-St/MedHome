package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.HealthPackageModel
import com.example.medhomeapp.model.PackageBookingModel
import com.example.medhomeapp.repository.HealthPackageRepo
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepo
import com.example.medhomeapp.repository.PackageBookingRepoImpl

class HealthPackageViewModel(
    private val packageRepo: HealthPackageRepo = HealthPackageRepoImpl(),
    private val bookingRepo: PackageBookingRepo = PackageBookingRepoImpl()
) : ViewModel() {


    val allPackages = mutableStateOf<List<HealthPackageModel>>(emptyList())
    val doctorPackages = mutableStateOf<List<HealthPackageModel>>(emptyList())
    val activePackages = mutableStateOf<List<HealthPackageModel>>(emptyList())
    val currentPackage = mutableStateOf<HealthPackageModel?>(null)


    val patientBookings = mutableStateOf<List<PackageBookingModel>>(emptyList())
    val doctorBookings = mutableStateOf<List<PackageBookingModel>>(emptyList())
    val packageBookings = mutableStateOf<List<PackageBookingModel>>(emptyList())


    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)



    fun createPackage(packageModel: HealthPackageModel, callback: (Boolean, String) -> Unit) {
        isLoading.value = true
        packageRepo.createPackage(packageModel) { success, message ->
            isLoading.value = false
            if (success) {
                successMessage.value = message
            } else {
                errorMessage.value = message
            }
            callback(success, message)
        }
    }

    fun getAllPackages() {
        isLoading.value = true
        packageRepo.getAllPackages { success, message, packages ->
            isLoading.value = false
            if (success) {
                allPackages.value = packages
            } else {
                errorMessage.value = message
            }
        }
    }

    fun getPackagesByDoctor(doctorId: String) {
        isLoading.value = true
        packageRepo.getPackagesByDoctor(doctorId) { success, message, packages ->
            isLoading.value = false
            if (success) {
                doctorPackages.value = packages
            } else {
                errorMessage.value = message
            }
        }
    }

    fun getActivePackages() {
        isLoading.value = true
        packageRepo.getActivePackages { success, message, packages ->
            isLoading.value = false
            if (success) {
                activePackages.value = packages
            } else {
                errorMessage.value = message
            }
        }
    }

    fun getPackageById(packageId: String, callback: (HealthPackageModel?) -> Unit) {
        isLoading.value = true
        packageRepo.getPackageById(packageId) { success, message, pkg ->
            isLoading.value = false
            if (success && pkg != null) {
                currentPackage.value = pkg
                callback(pkg)
            } else {
                errorMessage.value = message
                callback(null)
            }
        }
    }

    fun updatePackage(packageId: String, packageModel: HealthPackageModel, callback: (Boolean, String) -> Unit) {
        isLoading.value = true
        packageRepo.updatePackage(packageId, packageModel) { success, message ->
            isLoading.value = false
            if (success) {
                successMessage.value = message
            } else {
                errorMessage.value = message
            }
            callback(success, message)
        }
    }

    fun deletePackage(packageId: String, callback: (Boolean, String) -> Unit) {
        isLoading.value = true
        packageRepo.deletePackage(packageId) { success, message ->
            isLoading.value = false
            if (success) {
                successMessage.value = message
            } else {
                errorMessage.value = message
            }
            callback(success, message)
        }
    }

    fun getPackagesByCategory(category: String, callback: (List<HealthPackageModel>) -> Unit) {
        isLoading.value = true
        packageRepo.getPackagesByCategory(category) { success, message, packages ->
            isLoading.value = false
            if (success) {
                callback(packages)
            } else {
                errorMessage.value = message
                callback(emptyList())
            }
        }
    }



    fun createBooking(bookingModel: PackageBookingModel, callback: (Boolean, String) -> Unit) {
        isLoading.value = true
        bookingRepo.createBooking(bookingModel) { success, message ->
            isLoading.value = false
            if (success) {
                successMessage.value = message
            } else {
                errorMessage.value = message
            }
            callback(success, message)
        }
    }

    fun getBookingsByPatient(patientId: String) {
        isLoading.value = true
        bookingRepo.getBookingsByPatient(patientId) { success, message, bookings ->
            isLoading.value = false
            if (success) {
                patientBookings.value = bookings
            } else {
                errorMessage.value = message
            }
        }
    }

    fun getBookingsByDoctor(doctorId: String) {
        isLoading.value = true
        bookingRepo.getBookingsByDoctor(doctorId) { success, message, bookings ->
            isLoading.value = false
            if (success) {
                doctorBookings.value = bookings
            } else {
                errorMessage.value = message
            }
        }
    }

    fun getBookingsByPackage(packageId: String) {
        isLoading.value = true
        bookingRepo.getBookingsByPackage(packageId) { success, message, bookings ->
            isLoading.value = false
            if (success) {
                packageBookings.value = bookings
            } else {
                errorMessage.value = message
            }
        }
    }

    fun cancelBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        isLoading.value = true
        bookingRepo.cancelBooking(bookingId) { success, message ->
            isLoading.value = false
            if (success) {
                successMessage.value = message
            } else {
                errorMessage.value = message
            }
            callback(success, message)
        }
    }


    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }
}