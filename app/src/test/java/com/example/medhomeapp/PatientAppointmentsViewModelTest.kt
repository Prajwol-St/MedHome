package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepo
import com.example.medhomeapp.viewmodel.PatientAppointmentsViewModel
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

class PatientAppointmentsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadUpcomingAppointments_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        val mockAppointments = listOf(
            AppointmentModel(
                appointmentId = "1",
                patientId = "patient123",
                doctorId = "doctor1",
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
    fun cancelAppointment_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Appointment cancelled")
            null
        }.`when`(repo).cancelAppointment(any(), any(), any(), any())

        viewModel.cancelAppointment("appointment123", "Change of plans")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Appointment cancelled", result?.second)

        verify(repo).cancelAppointment(eq("appointment123"), eq("Change of plans"), any(), any())
    }

    @Test
    fun cancelAppointment_error_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(false, "Failed to cancel")
            null
        }.`when`(repo).cancelAppointment(any(), any(), any(), any())

        viewModel.cancelAppointment("appointment123", "Change of plans")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to cancel", result?.second)

        verify(repo).cancelAppointment(eq("appointment123"), eq("Change of plans"), any(), any())
    }

    @Test
    fun rescheduleAppointment_success_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(4)
            callback(true, "Appointment rescheduled")
            null
        }.`when`(repo).rescheduleAppointment(any(), any(), any(), any(), any())

        viewModel.rescheduleAppointment("appointment123", "2024-01-20", "11:00 AM", "slot456")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Appointment rescheduled", result?.second)

        verify(repo).rescheduleAppointment(
            eq("appointment123"),
            eq("2024-01-20"),
            eq("11:00 AM"),
            eq("slot456"),
            any()
        )
    }

    @Test
    fun rescheduleAppointment_error_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(4)
            callback(false, "Failed to reschedule")
            null
        }.`when`(repo).rescheduleAppointment(any(), any(), any(), any(), any())

        viewModel.rescheduleAppointment("appointment123", "2024-01-20", "11:00 AM", "slot456")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to reschedule", result?.second)

        verify(repo).rescheduleAppointment(
            eq("appointment123"),
            eq("2024-01-20"),
            eq("11:00 AM"),
            eq("slot456"),
            any()
        )
    }

    @Test
    fun clearOperationResult_test() = runBlocking {
        val repo = mock<AppointmentManagementRepo>()
        val viewModel = PatientAppointmentsViewModel(repo, "patient123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Success")
            null
        }.`when`(repo).cancelAppointment(any(), any(), any(), any())

        viewModel.cancelAppointment("appointment123", "Reason")
        assertEquals(true, viewModel.operationResult.first()?.first)

        viewModel.clearOperationResult()
        assertNull(viewModel.operationResult.first())
    }
}