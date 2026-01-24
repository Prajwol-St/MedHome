package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.repository.NotificationRepository
import java.util.UUID

class MedicineReminderViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val medicineList = mutableStateOf<List<MedicineReminderModel>>(emptyList())
    val selectedMedicine = mutableStateOf<MedicineReminderModel?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

        fun loadMedicineReminders(userId: String) {
        isLoading.value = true
        errorMessage.value = null

        repository.getMedicineReminders(userId) { medicines ->
            medicineList.value = medicines.sortedByDescending { it.createdAt }
            isLoading.value = false
        }
    }

        fun getMedicineById(userId: String, medicineId: String) {
        isLoading.value = true
        errorMessage.value = null

        repository.getMedicineReminder(userId, medicineId) { medicine ->
            selectedMedicine.value = medicine
            isLoading.value = false
        }
    }

        fun addMedicineReminder(
        context: android.content.Context,
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
                // Schedule notifications
                com.example.medhomeapp.utils.MedicineNotificationIntegration.onMedicineAdded(context, medicine)
                loadMedicineReminders(userId) // Refresh the list
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

        fun updateMedicineReminder(
        context: android.content.Context,
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
                // Update scheduled notifications
                com.example.medhomeapp.utils.MedicineNotificationIntegration.onMedicineUpdated(context, updatedMedicine)
                loadMedicineReminders(medicine.userId)
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

        fun deleteMedicineReminder(
        context: android.content.Context,
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
                // Cancel scheduled notifications
                com.example.medhomeapp.utils.MedicineNotificationIntegration.onMedicineDeleted(context, medicineId)
                loadMedicineReminders(userId)
                onComplete(true)
            } else {
                errorMessage.value = message
                onComplete(false)
            }
        }
    }

        fun toggleMedicineReminder(
        context: android.content.Context,
        userId: String,
        medicineId: String,
        isEnabled: Boolean
    ) {
        errorMessage.value = null

        repository.toggleMedicineReminder(userId, medicineId, isEnabled) { success, message ->
            if (success) {
                // Update local list optimistically
                val medicine = medicineList.value.find { it.medicineId == medicineId }
                medicineList.value = medicineList.value.map { med ->
                    if (med.medicineId == medicineId) {
                        med.copy(isEnabled = isEnabled)
                    } else {
                        med
                    }
                }

                // Update scheduled notifications
                medicine?.let {
                    com.example.medhomeapp.utils.MedicineNotificationIntegration.onMedicineToggled(
                        context,
                        it.copy(isEnabled = isEnabled),
                        isEnabled
                    )
                }

                successMessage.value = if (isEnabled) "Reminder enabled" else "Reminder disabled"
            } else {
                errorMessage.value = message
                // Reload to get correct state
                loadMedicineReminders(userId)
            }
        }
    }

        fun getActiveMedicines(): List<MedicineReminderModel> {
        return medicineList.value.filter { it.isEnabled }
    }

        fun getInactiveMedicines(): List<MedicineReminderModel> {
        return medicineList.value.filter { !it.isEnabled }
    }

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