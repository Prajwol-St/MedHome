package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.repository.NotificationRepository
import java.util.UUID
import kotlin.collections.filter

class MedicineReminderViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val medicineList = mutableStateOf<List<MedicineReminderModel>>(emptyList())
    val selectedMedicine = mutableStateOf<MedicineReminderModel?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    /**
     * Load all medicine reminders for a user
     */
    fun loadMedicineReminders(userId: String) {
        isLoading.value = true
        errorMessage.value = null

        repository.getMedicineReminders(userId) { medicines ->
            medicineList.value = medicines.sortedByDescending { it.createdAt }
            isLoading.value = false
        }
    }

    /**
     * Get a specific medicine reminder
     */
    fun getMedicineById(userId: String, medicineId: String) {
        isLoading.value = true
        errorMessage.value = null

        repository.getMedicineReminder(userId, medicineId) { medicine ->
            selectedMedicine.value = medicine
            isLoading.value = false
        }
    }

    /**
     * Add a new medicine reminder
     */
    fun addMedicineReminder(
        userId: String,
        medicineName: String,
        dosage: String,
        reminderTimes: List<String>,
        frequency: String,
        instructions: String,
        startDate: Long,
        endDate: Long?,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (medicineName.isBlank()) {
            errorMessage.value = "Medicine name is required"
            onComplete(false)
            return
        }

        if (dosage.isBlank()) {
            errorMessage.value = "Dosage is required"
            onComplete(false)
            return
        }

        if (reminderTimes.isEmpty()) {
            errorMessage.value = "Please select at least one reminder time"
            onComplete(false)
            return
        }

        isLoading.value = true
        errorMessage.value = null
        successMessage.value = null

        val medicine = MedicineReminderModel(
            medicineId = UUID.randomUUID().toString(),
            userId = userId,
            medicineName = medicineName.trim(),
            dosage = dosage.trim(),
            reminderTimes = reminderTimes.sorted(), // Sort times chronologically
            frequency = frequency,
            isEnabled = true,
            startDate = startDate,
            endDate = endDate,
            instructions = instructions.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        repository.addMedicineReminder(medicine) { success, message ->
            isLoading.value = false

            if (success) {
                successMessage.value = "Medicine reminder added successfully"
                loadMedicineReminders(userId) // Refresh the list
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

    /**
     * Update an existing medicine reminder
     */
    fun updateMedicineReminder(
        medicine: MedicineReminderModel,
        medicineName: String,
        dosage: String,
        reminderTimes: List<String>,
        frequency: String,
        instructions: String,
        startDate: Long,
        endDate: Long?,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (medicineName.isBlank()) {
            errorMessage.value = "Medicine name is required"
            onComplete(false)
            return
        }

        if (dosage.isBlank()) {
            errorMessage.value = "Dosage is required"
            onComplete(false)
            return
        }

        if (reminderTimes.isEmpty()) {
            errorMessage.value = "Please select at least one reminder time"
            onComplete(false)
            return
        }

        isLoading.value = true
        errorMessage.value = null
        successMessage.value = null

        val updatedMedicine = medicine.copy(
            medicineName = medicineName.trim(),
            dosage = dosage.trim(),
            reminderTimes = reminderTimes.sorted(),
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            instructions = instructions.trim(),
            updatedAt = System.currentTimeMillis()
        )

        repository.updateMedicineReminder(updatedMedicine) { success, message ->
            isLoading.value = false

            if (success) {
                successMessage.value = "Medicine reminder updated successfully"
                loadMedicineReminders(medicine.userId)
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

    /**
     * Delete a medicine reminder
     */
    fun deleteMedicineReminder(
        userId: String,
        medicineId: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        isLoading.value = true
        errorMessage.value = null
        successMessage.value = null

        repository.deleteMedicineReminder(userId, medicineId) { success, message ->
            isLoading.value = false

            if (success) {
                successMessage.value = "Medicine reminder deleted successfully"
                loadMedicineReminders(userId)
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

    /**
     * Toggle medicine reminder on/off
     */
    fun toggleMedicineReminder(
        userId: String,
        medicineId: String,
        isEnabled: Boolean
    ) {
        errorMessage.value = null

        repository.toggleMedicineReminder(userId, medicineId, isEnabled) { success, message ->
            if (success) {
                // Update local list optimistically
                medicineList.value = medicineList.value.map { medicine ->
                    if (medicine.medicineId == medicineId) {
                        medicine.copy(isEnabled = isEnabled)
                    } else {
                        medicine
                    }
                }
                successMessage.value = if (isEnabled) "Reminder enabled" else "Reminder disabled"
            } else {
                errorMessage.value = message
                // Reload to get correct state
                loadMedicineReminders(userId)
            }
        }
    }

    /**
     * Get active (enabled) medicine reminders
     */
    fun getActiveMedicines(): List<MedicineReminderModel> {
        return medicineList.value.filter { it.isEnabled }
    }

    /**
     * Get inactive (disabled) medicine reminders
     */
    fun getInactiveMedicines(): List<MedicineReminderModel> {
        return medicineList.value.filter { !it.isEnabled }
    }

    /**
     * Check if a medicine is currently active (within start/end date and enabled)
     */
    fun isMedicineActive(medicine: MedicineReminderModel): Boolean {
        val now = System.currentTimeMillis()

        if (!medicine.isEnabled) return false
        if (now < medicine.startDate) return false
        if (medicine.endDate != null && now > medicine.endDate) return false

        return true
    }


    fun getTodaysMedicines(): List<MedicineReminderModel> {
        return medicineList.value.filter { medicine ->
            isMedicineActive(medicine) && medicine.frequency == "daily"
        }
    }


    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }


    fun clearSelectedMedicine() {
        selectedMedicine.value = null
    }


    fun isValidTimeFormat(time: String): Boolean {
        return try {
            val parts = time.split(":")
            if (parts.size != 2) return false

            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            hour in 0..23 && minute in 0..59
        } catch (e: Exception) {
            false
        }
    }


    fun sortReminderTimes(times: List<String>): List<String> {
        return times.sortedBy { time ->
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            hour * 60 + minute
        }
    }
}