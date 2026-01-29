package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.NotificationPreferencesModel
import com.example.medhomeapp.repository.NotificationRepository
import com.example.medhomeapp.viewmodel.NotificationSettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class NotificationSettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadPreferences_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        val mockPreferences = NotificationPreferencesModel(
            userId = "user123",
            appointmentRemindersEnabled = true,
            medicineRemindersEnabled = true,
            bookingConfirmationsEnabled = true,
            reminderSound = true,
            vibration = true
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(NotificationPreferencesModel?) -> Unit>(1)
            callback(mockPreferences)
            null
        }.`when`(repo).getNotificationPreferences(any(), any())

        viewModel.loadPreferences("user123")

        assertEquals(mockPreferences, viewModel.preferences.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).getNotificationPreferences(eq("user123"), any())
    }

    @Test
    fun toggleAppointmentReminders_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            appointmentRemindersEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Success")
            null
        }.`when`(repo).toggleAppointmentRemindersGlobally(any(), any(), any())

        viewModel.toggleAppointmentReminders("user123", true)

        assertEquals(true, viewModel.preferences.value?.appointmentRemindersEnabled)
        assertNull(viewModel.errorMessage.value)

        verify(repo).toggleAppointmentRemindersGlobally(eq("user123"), eq(true), any())
    }

    @Test
    fun toggleAppointmentReminders_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            appointmentRemindersEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to toggle")
            null
        }.`when`(repo).toggleAppointmentRemindersGlobally(any(), any(), any())

        viewModel.toggleAppointmentReminders("user123", true)

        assertEquals("Failed to toggle", viewModel.errorMessage.value)

        verify(repo).toggleAppointmentRemindersGlobally(eq("user123"), eq(true), any())
    }

    @Test
    fun toggleMedicineReminders_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            medicineRemindersEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Success")
            null
        }.`when`(repo).toggleMedicineRemindersGlobally(any(), any(), any())

        viewModel.toggleMedicineReminders("user123", true)

        assertEquals(true, viewModel.preferences.value?.medicineRemindersEnabled)
        assertNull(viewModel.errorMessage.value)

        verify(repo).toggleMedicineRemindersGlobally(eq("user123"), eq(true), any())
    }

    @Test
    fun toggleMedicineReminders_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            medicineRemindersEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to toggle")
            null
        }.`when`(repo).toggleMedicineRemindersGlobally(any(), any(), any())

        viewModel.toggleMedicineReminders("user123", true)

        assertEquals("Failed to toggle", viewModel.errorMessage.value)

        verify(repo).toggleMedicineRemindersGlobally(eq("user123"), eq(true), any())
    }

    @Test
    fun toggleBookingConfirmations_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            bookingConfirmationsEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Success")
            null
        }.`when`(repo).updateNotificationPreferences(any(), any())

        viewModel.toggleBookingConfirmations("user123", true)

        assertEquals(true, viewModel.preferences.value?.bookingConfirmationsEnabled)
        assertNull(viewModel.errorMessage.value)

        verify(repo).updateNotificationPreferences(any(), any())
    }

    @Test
    fun toggleBookingConfirmations_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationSettingsViewModel(repo)

        viewModel.preferences.value = NotificationPreferencesModel(
            userId = "user123",
            bookingConfirmationsEnabled = false
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to update")
            null
        }.`when`(repo).updateNotificationPreferences(any(), any())

        viewModel.toggleBookingConfirmations("user123", true)

        assertEquals("Failed to update", viewModel.errorMessage.value)

        verify(repo).updateNotificationPreferences(any(), any())
    }
}