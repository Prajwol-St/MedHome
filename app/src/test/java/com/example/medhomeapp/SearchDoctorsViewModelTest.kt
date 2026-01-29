package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.DoctorSearchFilterModel
import com.example.medhomeapp.repository.DoctorRepo
import com.example.medhomeapp.viewmodel.SearchDoctorsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SearchDoctorsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadDoctors_success_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        val mockDoctors = listOf(
            DoctorModel(
                id = "1",
                name = "Dr. Smith",
                specialization = "Cardiology",
                experience = 10,
                consultationFee = 500.0,
                averageRating = 4.5f
            ),
            DoctorModel(
                id = "2",
                name = "Dr. Jones",
                specialization = "Neurology",
                experience = 15,
                consultationFee = 700.0,
                averageRating = 4.8f
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<DoctorModel>) -> Unit>(0)
            callback(true, "", mockDoctors)
            null
        }.`when`(repo).getAllDoctors(any())

        viewModel.loadDoctors()

        assertEquals(mockDoctors, viewModel.allDoctors.first())
        assertEquals(mockDoctors, viewModel.filteredDoctors.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getAllDoctors(any())
    }

    @Test
    fun loadDoctors_error_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<DoctorModel>) -> Unit>(0)
            callback(false, "Failed to load doctors", emptyList())
            null
        }.`when`(repo).getAllDoctors(any())

        viewModel.loadDoctors()

        assertEquals(emptyList<DoctorModel>(), viewModel.allDoctors.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getAllDoctors(any())
    }

    @Test
    fun searchDoctors_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

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
        viewModel.searchDoctors("Smith")

        assertEquals("Smith", viewModel.searchQuery.first())
    }

    @Test
    fun filterBySpecialization_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        viewModel.filterBySpecialization("Cardiology")

        assertEquals("Cardiology", viewModel.currentFilter.first().specialization)
    }

    @Test
    fun filterByRating_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        viewModel.filterByRating(4.5f)

        assertEquals(4.5f, viewModel.currentFilter.first().minRating)
    }

    @Test
    fun filterByFeeRange_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        viewModel.filterByFeeRange(100.0, 500.0)

        assertEquals(100.0, viewModel.currentFilter.first().minFee, 0.01)
        assertEquals(500.0, viewModel.currentFilter.first().maxFee, 0.01)
    }

    @Test
    fun sortDoctors_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        viewModel.sortDoctors("rating_desc")

        assertEquals("rating_desc", viewModel.currentFilter.first().sortBy)
    }

    @Test
    fun clearFilters_test() = runBlocking {
        val repo = mock<DoctorRepo>()
        val viewModel = SearchDoctorsViewModel(repo)

        val mockDoctors = listOf(
            DoctorModel(
                id = "1",
                name = "Dr. Smith",
                specialization = "Cardiology",
                experience = 10
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<DoctorModel>) -> Unit>(0)
            callback(true, "", mockDoctors)
            null
        }.`when`(repo).getAllDoctors(any())

        viewModel.loadDoctors()
        viewModel.searchDoctors("Smith")
        viewModel.filterBySpecialization("Cardiology")

        viewModel.clearFilters()

        assertEquals("", viewModel.searchQuery.first())
        assertEquals(DoctorSearchFilterModel(), viewModel.currentFilter.first())
        assertEquals(mockDoctors, viewModel.filteredDoctors.first())
    }
}