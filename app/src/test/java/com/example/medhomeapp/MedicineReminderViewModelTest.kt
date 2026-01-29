package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.MedicineReminderModel
import com.example.medhomeapp.repository.NotificationRepository
import com.example.medhomeapp.viewmodel.MedicineReminderViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class MedicineReminderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadMedicineReminders_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)

        val mockMedicines = listOf(
            MedicineReminderModel(
                medicineId = "1",
                userId = "user123",
                medicineName = "Aspirin",
                dosage = "100mg",
                reminderTimes = listOf("08:00", "20:00"),
                frequency = "daily"
            ),
            MedicineReminderModel(
                medicineId = "2",
                userId = "user123",
                medicineName = "Vitamin D",
                dosage = "500mg",
                reminderTimes = listOf("09:00"),
                frequency = "daily"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<MedicineReminderModel>) -> Unit>(1)
            callback(mockMedicines)
            null
        }.`when`(repo).getMedicineReminders(any(), any())

        viewModel.loadMedicineReminders("user123")

        assertEquals(mockMedicines.sortedByDescending { it.createdAt }, viewModel.medicineList.value)
        assertEquals(false, viewModel.isLoading.value)

        verify(repo).getMedicineReminders(eq("user123"), any())
    }

    @Test
    fun deleteMedicineReminder_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)
        val context = mock<android.content.Context>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Medicine reminder deleted successfully")
            null
        }.`when`(repo).deleteMedicineReminder(any(), any(), any())

        var callbackResult = false
        viewModel.deleteMedicineReminder(context, "user123", "medicine123") { success ->
            callbackResult = success
        }

        assertEquals("Medicine reminder deleted successfully", viewModel.successMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(true, callbackResult)

        verify(repo).deleteMedicineReminder(eq("user123"), eq("medicine123"), any())
    }

    @Test
    fun deleteMedicineReminder_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)
        val context = mock<android.content.Context>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to delete medicine reminder")
            null
        }.`when`(repo).deleteMedicineReminder(any(), any(), any())

        var callbackResult = true
        viewModel.deleteMedicineReminder(context, "user123", "medicine123") { success ->
            callbackResult = success
        }

        assertEquals("Failed to delete medicine reminder", viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(false, callbackResult)

        verify(repo).deleteMedicineReminder(eq("user123"), eq("medicine123"), any())
    }

    @Test
    fun toggleMedicineReminder_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)
        val context = mock<android.content.Context>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Success")
            null
        }.`when`(repo).toggleMedicineReminder(any(), any(), any(), any())

        viewModel.toggleMedicineReminder(context, "user123", "medicine123", true)

        assertEquals("Reminder enabled", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).toggleMedicineReminder(eq("user123"), eq("medicine123"), eq(true), any())
    }

    @Test
    fun toggleMedicineReminder_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)
        val context = mock<android.content.Context>()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(false, "Failed to toggle")
            null
        }.`when`(repo).toggleMedicineReminder(any(), any(), any(), any())

        viewModel.toggleMedicineReminder(context, "user123", "medicine123", true)

        assertEquals("Failed to toggle", viewModel.errorMessage.value)

        verify(repo).toggleMedicineReminder(eq("user123"), eq("medicine123"), eq(true), any())
    }

    @Test
    fun clearMessages_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = MedicineReminderViewModel(repo)

        viewModel.errorMessage.value = "Error"
        viewModel.successMessage.value = "Success"

        viewModel.clearMessages()

        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
    }
}