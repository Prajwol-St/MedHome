package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import com.example.medhomeapp.viewmodel.DoctorAvailabilityViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DoctorAvailabilityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSlotsByDate_success_test() = runBlocking {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        val mockSlots = listOf(
            TimeSlot(
                id = "slot1",
                doctorId = "doctor123",
                date = "2024-01-15",
                startTime = "10:00 AM",
                endTime = "10:30 AM",
                isAvailable = true,
                isBooked = false
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<TimeSlot>) -> Unit>(2)
            callback(mockSlots)
            null
        }.`when`(repo).getSlotsByDate(any(), any(), any())

        viewModel.loadSlotsByDate("2024-01-15")

        assertEquals(mockSlots, viewModel.slotsByDate.first())

        verify(repo).getSlotsByDate(eq("doctor123"), eq("2024-01-15"), any())
    }

    @Test
    fun loadAvailableSlots_success_test() = runBlocking {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        val mockSlots = listOf(
            TimeSlot(
                id = "slot1",
                doctorId = "doctor123",
                date = "2024-01-15",
                startTime = "10:00 AM",
                endTime = "10:30 AM",
                isAvailable = true,
                isBooked = false
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<TimeSlot>) -> Unit>(2)
            callback(mockSlots)
            null
        }.`when`(repo).getAvailableSlots(any(), any(), any())

        viewModel.loadAvailableSlots("2024-01-15")

        assertEquals(mockSlots, viewModel.availableSlots.first())

        verify(repo).getAvailableSlots(eq("doctor123"), eq("2024-01-15"), any())
    }

    @Test
    fun addSlot_test() {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        viewModel.addSlot("2024-01-15", "10:00 AM", "10:30 AM")

        verify(repo).addTimeSlot(any())
    }

    @Test
    fun deleteSlot_test() {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        viewModel.deleteSlot("2024-01-15", "slot123")

        verify(repo).deleteTimeSlot(eq("doctor123"), eq("2024-01-15"), eq("slot123"))
    }

    @Test
    fun markSlotBooked_test() {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        viewModel.markSlotBooked("2024-01-15", "slot123", "appointment123")

        verify(repo).updateSlotBookingStatus(
            eq("doctor123"),
            eq("2024-01-15"),
            eq("slot123"),
            eq(true),
            eq("appointment123")
        )
    }

    @Test
    fun markSlotAvailable_test() {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorAvailabilityViewModel(repo, "doctor123")

        viewModel.markSlotAvailable("2024-01-15", "slot123")

        verify(repo).updateSlotBookingStatus(
            eq("doctor123"),
            eq("2024-01-15"),
            eq("slot123"),
            eq(false),
            eq("")
        )
    }
}