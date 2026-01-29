package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepo
import com.example.medhomeapp.viewmodel.DoctorAppointmentsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DoctorAppointmentsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadUpcomingAppointments_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        val mockAppointments = listOf(
            AppointmentModel(
                appointmentId = "1",
                patientId = "patient1",
                doctorId = "doctor123",
                date = "2024-01-15",
                time = "10:00 AM"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<AppointmentModel>) -> Unit>(2)
            callback(mockAppointments)
            null
        }.`when`(repo).getUpcomingAppointments(any(), any(), any())

        viewModel.loadAllAppointments()

        assertEquals(mockAppointments, viewModel.upcomingAppointments.first())
        assertEquals(false, viewModel.isLoading.first())
    }

    @Test
    fun completeAppointment_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Appointment completed successfully")
            null
        }.`when`(repo).completeAppointment(any(), any(), any())

        viewModel.completeAppointment("appointment123", "Patient is healthy")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Appointment completed successfully", result?.second)

        verify(repo).completeAppointment(eq("appointment123"), eq("Patient is healthy"), any())
    }

    @Test
    fun completeAppointment_error_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to complete appointment")
            null
        }.`when`(repo).completeAppointment(any(), any(), any())

        viewModel.completeAppointment("appointment123", "Patient is healthy")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to complete appointment", result?.second)

        verify(repo).completeAppointment(eq("appointment123"), eq("Patient is healthy"), any())
    }

    @Test
    fun markAsNoShow_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Marked as no-show")
            null
        }.`when`(repo).markAsNoShow(any(), any())

        viewModel.markAsNoShow("appointment123")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Marked as no-show", result?.second)

        verify(repo).markAsNoShow(eq("appointment123"), any())
    }

    @Test
    fun markAsNoShow_error_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to mark as no-show")
            null
        }.`when`(repo).markAsNoShow(any(), any())

        viewModel.markAsNoShow("appointment123")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to mark as no-show", result?.second)

        verify(repo).markAsNoShow(eq("appointment123"), any())
    }

    @Test
    fun cancelAppointment_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Appointment cancelled")
            null
        }.`when`(repo).cancelAppointment(any(), any(), any(), any())

        viewModel.cancelAppointment("appointment123", "Emergency")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Appointment cancelled", result?.second)

        verify(repo).cancelAppointment(eq("appointment123"), eq("Emergency"), any(), any())
    }

    @Test
    fun cancelAppointment_error_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(false, "Failed to cancel")
            null
        }.`when`(repo).cancelAppointment(any(), any(), any(), any())

        viewModel.cancelAppointment("appointment123", "Emergency")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to cancel", result?.second)

        verify(repo).cancelAppointment(eq("appointment123"), eq("Emergency"), any(), any())
    }

    @Test
    fun clearOperationResult_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = DoctorAppointmentsViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Success")
            null
        }.`when`(repo).markAsNoShow(any(), any())

        viewModel.markAsNoShow("appointment123")
        assertEquals(true, viewModel.operationResult.first()?.first)

        viewModel.clearOperationResult()
        assertNull(viewModel.operationResult.first())
    }
}