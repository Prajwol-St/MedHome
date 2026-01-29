package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.RatingModel
import com.example.medhomeapp.repository.RatingRepo
import com.example.medhomeapp.viewmodel.RatingViewModel
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

class RatingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun submitRating_success_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        viewModel.setRating(4.5f)
        viewModel.setReview("Great doctor!")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Rating submitted successfully")
            null
        }.`when`(repo).addRating(any(), any())

        viewModel.submitRating("appointment123", "patient123", "John Doe", "doctor123")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Rating submitted successfully", result?.second)
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).addRating(any(), any())
    }

    @Test
    fun submitRating_error_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        viewModel.setRating(4.5f)
        viewModel.setReview("Great doctor!")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to submit rating")
            null
        }.`when`(repo).addRating(any(), any())

        viewModel.submitRating("appointment123", "patient123", "John Doe", "doctor123")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to submit rating", result?.second)
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).addRating(any(), any())
    }

    @Test
    fun loadDoctorRatings_success_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        val mockRatings = listOf(
            RatingModel(
                ratingId = "1",
                appointmentId = "appointment1",
                patientId = "patient1",
                patientName = "John Doe",
                doctorId = "doctor123",
                rating = 4.5f,
                review = "Great doctor!"
            ),
            RatingModel(
                ratingId = "2",
                appointmentId = "appointment2",
                patientId = "patient2",
                patientName = "Jane Smith",
                doctorId = "doctor123",
                rating = 5.0f,
                review = "Excellent service!"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<RatingModel>) -> Unit>(1)
            callback(mockRatings)
            null
        }.`when`(repo).getRatingsByDoctor(any(), any())

        viewModel.loadDoctorRatings("doctor123")

        assertEquals(mockRatings, viewModel.doctorRatings.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getRatingsByDoctor(eq("doctor123"), any())
    }

    @Test
    fun editRating_success_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Rating updated successfully")
            null
        }.`when`(repo).editRating(any(), any(), any(), any())

        viewModel.editRating("rating123", 5.0f, "Updated review")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Rating updated successfully", result?.second)
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).editRating(eq("rating123"), eq(5.0f), eq("Updated review"), any())
    }

    @Test
    fun editRating_error_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(false, "Failed to update rating")
            null
        }.`when`(repo).editRating(any(), any(), any(), any())

        viewModel.editRating("rating123", 5.0f, "Updated review")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to update rating", result?.second)
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).editRating(eq("rating123"), eq(5.0f), eq("Updated review"), any())
    }

    @Test
    fun clearForm_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        viewModel.setRating(4.5f)
        viewModel.setReview("Great doctor!")

        viewModel.clearForm()

        assertEquals(0f, viewModel.rating.first())
        assertEquals("", viewModel.review.first())
    }

    @Test
    fun clearOperationResult_test() = runBlocking {
        val repo = mock<RatingRepo>()
        val viewModel = RatingViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Success")
            null
        }.`when`(repo).editRating(any(), any(), any(), any())

        viewModel.editRating("rating123", 5.0f, "Review")
        assertEquals(true, viewModel.operationResult.first()?.first)

        viewModel.clearOperationResult()
        assertNull(viewModel.operationResult.first())
    }
}