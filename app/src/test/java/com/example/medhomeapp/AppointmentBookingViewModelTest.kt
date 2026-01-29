package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.AppointmentBookingRepo
import com.example.medhomeapp.viewmodel.AppointmentBookingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AppointmentBookingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: AppointmentBookingRepo
    private lateinit var viewModel: AppointmentBookingViewModel

    @Before
    fun setup() {
        repo = mock()
        viewModel = AppointmentBookingViewModel(repo)
    }

    @Test
    fun initialState_hasNullValues_test() = runBlocking {
        val bookingState = viewModel.bookingState.first()
        assertNull(bookingState.first)
        assertNull(bookingState.second)

        val slot = viewModel.slot.first()
        assertNull(slot)
    }

    @Test
    fun loadSlot_success_setsSlot_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val slotId = "slot456"
        val timeSlot = TimeSlot(
            id = slotId,
            doctorId = doctorId,
            date = date,
            day = "Monday",
            startTime = "09:00",
            endTime = "09:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(timeSlot)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())

        viewModel.loadSlot(doctorId, date, slotId)

        Thread.sleep(100)

        val slot = viewModel.slot.first()
        assertEquals(timeSlot, slot)
        assertEquals(slotId, slot?.id)
        assertEquals(doctorId, slot?.doctorId)
        assertEquals(date, slot?.date)
        assertEquals("Monday", slot?.day)
        assertEquals("09:00", slot?.startTime)
        assertEquals("09:30", slot?.endTime)
        assertEquals(30, slot?.duration)
        assertTrue(slot?.isAvailable ?: false)
        assertFalse(slot?.isBooked ?: true)
        assertEquals("", slot?.appointmentId)

        verify(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())
    }

    @Test
    fun loadSlot_withNullResult_setsNullSlot_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val slotId = "slot456"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(null)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())

        viewModel.loadSlot(doctorId, date, slotId)

        Thread.sleep(100)

        val slot = viewModel.slot.first()
        assertNull(slot)

        verify(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())
    }

    @Test
    fun loadSlot_withBookedSlot_loadsCorrectly_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val slotId = "slot456"

        val bookedSlot = TimeSlot(
            id = slotId,
            doctorId = doctorId,
            date = date,
            day = "Monday",
            startTime = "14:00",
            endTime = "14:30",
            duration = 30,
            isAvailable = false,
            isBooked = true,
            appointmentId = "apt999"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(bookedSlot)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())

        viewModel.loadSlot(doctorId, date, slotId)
        Thread.sleep(100)

        val slot = viewModel.slot.first()
        assertEquals(bookedSlot, slot)
        assertTrue(slot?.isBooked ?: false)
        assertFalse(slot?.isAvailable ?: true)
        assertEquals("apt999", slot?.appointmentId)
    }

    @Test
    fun loadSlot_withUnavailableSlot_loadsCorrectly_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val slotId = "slot789"

        val unavailableSlot = TimeSlot(
            id = slotId,
            doctorId = doctorId,
            date = date,
            day = "Tuesday",
            startTime = "16:00",
            endTime = "16:30",
            duration = 30,
            isAvailable = false,
            isBooked = false,
            appointmentId = ""
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(unavailableSlot)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())

        viewModel.loadSlot(doctorId, date, slotId)
        Thread.sleep(100)

        val slot = viewModel.slot.first()
        assertEquals(unavailableSlot, slot)
        assertFalse(slot?.isAvailable ?: true)
        assertFalse(slot?.isBooked ?: true)
    }

    @Test
    fun loadSlot_multipleTimes_updatesSlot_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"

        // Load first slot
        val slot1 = TimeSlot(
            id = "slot1",
            doctorId = doctorId,
            date = date,
            day = "Monday",
            startTime = "09:00",
            endTime = "09:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(slot1)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq("slot1"), any())

        viewModel.loadSlot(doctorId, date, "slot1")
        Thread.sleep(100)

        var loadedSlot = viewModel.slot.first()
        assertEquals("slot1", loadedSlot?.id)

        // Load second slot
        val slot2 = TimeSlot(
            id = "slot2",
            doctorId = doctorId,
            date = date,
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = false,
            isBooked = true,
            appointmentId = "apt123"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(slot2)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq("slot2"), any())

        viewModel.loadSlot(doctorId, date, "slot2")
        Thread.sleep(100)

        loadedSlot = viewModel.slot.first()
        assertEquals("slot2", loadedSlot?.id)
        assertTrue(loadedSlot?.isBooked ?: false)
        assertEquals("apt123", loadedSlot?.appointmentId)
    }

    @Test
    fun loadSlot_withDifferentDurations_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"

        val durations = listOf(15, 30, 45, 60)

        durations.forEachIndexed { index, duration ->
            val slot = TimeSlot(
                id = "slot$index",
                doctorId = doctorId,
                date = date,
                day = "Monday",
                startTime = "09:00",
                endTime = "09:${duration.toString().padStart(2, '0')}",
                duration = duration,
                isAvailable = true,
                isBooked = false,
                appointmentId = ""
            )

            doAnswer { invocation ->
                val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
                callback(slot)
                null
            }.`when`(repo).getSlot(eq(doctorId), eq(date), eq("slot$index"), any())

            viewModel.loadSlot(doctorId, date, "slot$index")
            Thread.sleep(100)

            val loadedSlot = viewModel.slot.first()
            assertEquals(duration, loadedSlot?.duration)
        }
    }

    @Test
    fun book_success_setsSuccessState_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        val reason = "Regular checkup"
        val successMessage = "Appointment booked successfully"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, successMessage)
            null
        }.`when`(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())

        viewModel.book(slot, patient, reason)

        // Wait a bit for async operations
        Thread.sleep(100)

        val bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertEquals(successMessage, bookingState.second)

        verify(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())
    }

    @Test
    fun book_failure_setsErrorState_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        val reason = "Regular checkup"
        val errorMessage = "Slot already booked"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, errorMessage)
            null
        }.`when`(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())

        viewModel.book(slot, patient, reason)

        // Wait a bit for async operations
        Thread.sleep(100)

        val bookingState = viewModel.bookingState.first()
        assertEquals(false, bookingState.first)
        assertEquals(errorMessage, bookingState.second)

        verify(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())
    }

    @Test
    fun book_withNullMessage_setsCorrectState_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, null)
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "Checkup")

        // Wait a bit for async operations
        Thread.sleep(100)

        val bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertNull(bookingState.second)
    }

    @Test
    fun book_withDifferentReasons_callsRepoCorrectly_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        val reasons = listOf("Regular checkup", "Follow-up", "Emergency", "Consultation")

        reasons.forEach { reason ->
            doAnswer { invocation ->
                val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
                callback(true, "Booked for: $reason")
                null
            }.`when`(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())

            viewModel.book(slot, patient, reason)
            Thread.sleep(100)

            verify(repo).bookAppointment(eq(slot), eq(patient), eq(reason), any())
        }
    }

    @Test
    fun clearBookingState_resetsState_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Success")
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "Checkup")
        Thread.sleep(100)

        // Verify state is set
        var bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertEquals("Success", bookingState.second)

        // Clear state
        viewModel.clearBookingState()

        // Verify state is cleared
        bookingState = viewModel.bookingState.first()
        assertNull(bookingState.first)
        assertNull(bookingState.second)
    }

    @Test
    fun clearBookingState_whenAlreadyNull_test() = runBlocking {
        // Clear when already null
        viewModel.clearBookingState()

        val bookingState = viewModel.bookingState.first()
        assertNull(bookingState.first)
        assertNull(bookingState.second)
    }

    @Test
    fun multipleBookingAttempts_updatesStateCorrectly_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        // First booking fails
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, "Network error")
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "Checkup")
        Thread.sleep(100)

        var bookingState = viewModel.bookingState.first()
        assertEquals(false, bookingState.first)
        assertEquals("Network error", bookingState.second)

        // Clear state for retry
        viewModel.clearBookingState()
        Thread.sleep(50)

        // Second booking succeeds
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Success")
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "Checkup")
        Thread.sleep(100)

        bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertEquals("Success", bookingState.second)
    }

    @Test
    fun book_withDifferentPatients_callsRepoCorrectly_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient1 = UserModel(
            id = "patient1",
            name = "John Doe",
            email = "john@example.com",
            contact = "1111111111",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        val patient2 = UserModel(
            id = "patient2",
            name = "Jane Smith",
            email = "jane@example.com",
            contact = "2222222222",
            dateOfBirth = "1985-05-15",
            gender = "Female",
            address = "456 Oak Ave"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Booking successful")
            null
        }.`when`(repo).bookAppointment(eq(slot), eq(patient1), any(), any())

        viewModel.book(slot, patient1, "Checkup")
        Thread.sleep(100)

        verify(repo).bookAppointment(eq(slot), eq(patient1), any(), any())

        viewModel.clearBookingState()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Booking successful")
            null
        }.`when`(repo).bookAppointment(eq(slot), eq(patient2), any(), any())

        viewModel.book(slot, patient2, "Consultation")
        Thread.sleep(100)

        verify(repo).bookAppointment(eq(slot), eq(patient2), any(), any())
    }

    @Test
    fun loadSlot_thenBook_workflow_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val slotId = "slot456"

        val timeSlot = TimeSlot(
            id = slotId,
            doctorId = doctorId,
            date = date,
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        // Load slot first
        doAnswer { invocation ->
            val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
            callback(timeSlot)
            null
        }.`when`(repo).getSlot(eq(doctorId), eq(date), eq(slotId), any())

        viewModel.loadSlot(doctorId, date, slotId)
        Thread.sleep(100)

        val slot = viewModel.slot.first()
        assertEquals(timeSlot, slot)
        assertTrue(slot?.isAvailable ?: false)
        assertFalse(slot?.isBooked ?: true)

        // Then book
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Booked successfully")
            null
        }.`when`(repo).bookAppointment(eq(timeSlot), eq(patient), any(), any())

        viewModel.book(timeSlot, patient, "Regular checkup")
        Thread.sleep(100)

        val bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertEquals("Booked successfully", bookingState.second)
    }

    @Test
    fun book_failsThenClearThenSucceeds_test() = runBlocking {
        val slot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = true,
            isBooked = false,
            appointmentId = ""
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        // First attempt fails
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, "Slot unavailable")
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "First attempt")
        Thread.sleep(100)

        var bookingState = viewModel.bookingState.first()
        assertEquals(false, bookingState.first)
        assertEquals("Slot unavailable", bookingState.second)

        // Clear the error state
        viewModel.clearBookingState()
        Thread.sleep(50)

        bookingState = viewModel.bookingState.first()
        assertNull(bookingState.first)
        assertNull(bookingState.second)

        // Second attempt succeeds
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(true, "Successfully booked")
            null
        }.`when`(repo).bookAppointment(any(), any(), any(), any())

        viewModel.book(slot, patient, "Second attempt")
        Thread.sleep(100)

        bookingState = viewModel.bookingState.first()
        assertEquals(true, bookingState.first)
        assertEquals("Successfully booked", bookingState.second)
    }

    @Test
    fun loadSlot_withDifferentDays_test() = runBlocking {
        val doctorId = "doctor123"
        val date = "2024-01-15"
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

        days.forEachIndexed { index, day ->
            val slot = TimeSlot(
                id = "slot$index",
                doctorId = doctorId,
                date = date,
                day = day,
                startTime = "09:00",
                endTime = "09:30",
                duration = 30,
                isAvailable = true,
                isBooked = false,
                appointmentId = ""
            )

            doAnswer { invocation ->
                val callback = invocation.getArgument<(TimeSlot?) -> Unit>(3)
                callback(slot)
                null
            }.`when`(repo).getSlot(eq(doctorId), eq(date), eq("slot$index"), any())

            viewModel.loadSlot(doctorId, date, "slot$index")
            Thread.sleep(100)

            val loadedSlot = viewModel.slot.first()
            assertEquals(day, loadedSlot?.day)
        }
    }

    @Test
    fun book_bookedSlot_shouldFail_test() = runBlocking {
        val alreadyBookedSlot = TimeSlot(
            id = "slot123",
            doctorId = "doctor456",
            date = "2024-01-15",
            day = "Monday",
            startTime = "10:00",
            endTime = "10:30",
            duration = 30,
            isAvailable = false,
            isBooked = true,
            appointmentId = "existing_apt"
        )

        val patient = UserModel(
            id = "patient789",
            name = "John Doe",
            email = "john@example.com",
            contact = "1234567890",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            address = "123 Main St"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?) -> Unit>(2)
            callback(false, "This slot is already booked")
            null
        }.`when`(repo).bookAppointment(eq(alreadyBookedSlot), eq(patient), any(), any())

        viewModel.book(alreadyBookedSlot, patient, "Checkup")
        Thread.sleep(100)

        val bookingState = viewModel.bookingState.first()
        assertEquals(false, bookingState.first)
        assertEquals("This slot is already booked", bookingState.second)
    }
}