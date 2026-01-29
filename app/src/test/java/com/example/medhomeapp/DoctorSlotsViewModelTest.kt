package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepo
import com.example.medhomeapp.viewmodel.DoctorSlotsViewModel
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

class DoctorSlotsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun observeSlots_success_test() = runBlocking {
        val repo = mock<DoctorAvailabilityRepo>()
        val viewModel = DoctorSlotsViewModel(repo)

        val mockSlots = listOf(
            TimeSlot(
                id = "slot1",
                doctorId = "doctor123",
                date = "2024-01-15",
                startTime = "10:00 AM",
                endTime = "10:30 AM",
                isAvailable = true,
                isBooked = false
            ),
            TimeSlot(
                id = "slot2",
                doctorId = "doctor123",
                date = "2024-01-15",
                startTime = "11:00 AM",
                endTime = "11:30 AM",
                isAvailable = true,
                isBooked = false
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<TimeSlot>) -> Unit>(1)
            callback(mockSlots)
            null
        }.`when`(repo).observeTimeSlots(any(), any())

        viewModel.observeSlots("doctor123")

        assertEquals(mockSlots, viewModel.slots.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).observeTimeSlots(eq("doctor123"), any())
    }
}