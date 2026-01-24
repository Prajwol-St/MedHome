package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.NotificationPreferencesModel
import com.example.medhomeapp.repository.NotificationRepository

class NotificationSettingsViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val preferences = mutableStateOf<NotificationPreferencesModel?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)


    fun loadPreferences(userId: String) {
        isLoading.value = true
        errorMessage.value = null

        repository.getNotificationPreferences(userId) { prefs ->
            preferences.value = prefs ?: NotificationPreferencesModel(userId = userId)
            isLoading.value = false
        }
    }


    fun toggleAppointmentReminders(userId: String, enabled: Boolean) {
        errorMessage.value = null

        repository.toggleAppointmentRemindersGlobally(userId, enabled) { success, message ->
            if (!success) {
                errorMessage.value = message
                // Revert the UI state on failure
                loadPreferences(userId)
            }
        }


        preferences.value = preferences.value?.copy(appointmentRemindersEnabled = enabled)
    }


    fun toggleMedicineReminders(userId: String, enabled: Boolean) {
        errorMessage.value = null

        repository.toggleMedicineRemindersGlobally(userId, enabled) { success, message ->
            if (!success) {
                errorMessage.value = message
                loadPreferences(userId)
            }
        }

        preferences.value = preferences.value?.copy(medicineRemindersEnabled = enabled)
    }


    fun toggleBookingConfirmations(userId: String, enabled: Boolean) {
        errorMessage.value = null

        val currentPrefs = preferences.value ?: return
        val updatedPrefs = currentPrefs.copy(bookingConfirmationsEnabled = enabled)

        repository.updateNotificationPreferences(updatedPrefs) { success, message ->
            if (!success) {
                errorMessage.value = message
                loadPreferences(userId)
            }
        }

        preferences.value = updatedPrefs
    }


    fun toggleReminderSound(userId: String, enabled: Boolean) {
        errorMessage.value = null

        val currentPrefs = preferences.value ?: return
        val updatedPrefs = currentPrefs.copy(reminderSound = enabled)

        repository.updateNotificationPreferences(updatedPrefs) { success, message ->
            if (!success) {
                errorMessage.value = message
                loadPreferences(userId)
            }
        }

        preferences.value = updatedPrefs
    }


    fun toggleVibration(userId: String, enabled: Boolean) {
        errorMessage.value = null

        val currentPrefs = preferences.value ?: return
        val updatedPrefs = currentPrefs.copy(vibration = enabled)

        repository.updateNotificationPreferences(updatedPrefs) { success, message ->
            if (!success) {
                errorMessage.value = message
                loadPreferences(userId)
            }
        }

        preferences.value = updatedPrefs
    }
}