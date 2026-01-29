package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.viewmodel.DoctorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DoctorViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadDoctors_success_test() = runBlocking {
        val repo = mock<DoctorRepoImpl>()
        val viewModel = DoctorViewModel(repo)

        val mockDoctors = listOf(
            DoctorModel(
                id = "1",
                name = "Dr. Smith",
                specialization = "Cardiology",
                experience = 10
            ),
            DoctorModel(
                id = "2",
                name = "Dr. Jones",
                specialization = "Neurology",
                experience = 15
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<DoctorModel>) -> Unit>(0)
            callback(true, "", mockDoctors)
            null
        }.`when`(repo).getAllDoctors(any())

        viewModel.loadDoctors()

        assertEquals(mockDoctors, viewModel.doctors.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getAllDoctors(any())
    }

    @Test
    fun loadDoctors_error_test() = runBlocking {
        val repo = mock<DoctorRepoImpl>()
        val viewModel = DoctorViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<DoctorModel>) -> Unit>(0)
            callback(false, "Failed to load doctors", emptyList())
            null
        }.`when`(repo).getAllDoctors(any())

        viewModel.loadDoctors()

        assertEquals(emptyList<DoctorModel>(), viewModel.doctors.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getAllDoctors(any())
    }
}