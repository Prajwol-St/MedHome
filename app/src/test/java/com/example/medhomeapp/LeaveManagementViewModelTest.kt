package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.DoctorLeaveModel
import com.example.medhomeapp.repository.LeaveManagementRepo
import com.example.medhomeapp.viewmodel.LeaveManagementViewModel
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

class LeaveManagementViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadLeaves_success_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        val mockLeaves = listOf(
            DoctorLeaveModel(
                leaveId = "1",
                doctorId = "doctor123",
                startDate = "2024-01-15",
                endDate = "2024-01-20",
                reason = "Vacation",
                leaveType = "Planned"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<DoctorLeaveModel>) -> Unit>(1)
            callback(mockLeaves)
            null
        }.`when`(repo).getLeaves(any(), any())

        viewModel.loadLeaves()

        assertEquals(mockLeaves, viewModel.leaves.first())
        assertEquals(false, viewModel.isLoading.first())

        verify(repo).getLeaves(eq("doctor123"), any())
    }

    @Test
    fun addLeave_success_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Leave added successfully")
            null
        }.`when`(repo).addLeave(any(), any())

        viewModel.addLeave("2024-01-15", "2024-01-20", "Vacation", "Planned")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Leave added successfully", result?.second)

        verify(repo).addLeave(any(), any())
    }

    @Test
    fun addLeave_error_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to add leave")
            null
        }.`when`(repo).addLeave(any(), any())

        viewModel.addLeave("2024-01-15", "2024-01-20", "Vacation", "Planned")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to add leave", result?.second)

        verify(repo).addLeave(any(), any())
    }

    @Test
    fun deleteLeave_success_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Leave deleted successfully")
            null
        }.`when`(repo).deleteLeave(any(), any(), any())

        viewModel.deleteLeave("leave123")

        val result = viewModel.operationResult.first()
        assertEquals(true, result?.first)
        assertEquals("Leave deleted successfully", result?.second)

        verify(repo).deleteLeave(eq("doctor123"), eq("leave123"), any())
    }

    @Test
    fun deleteLeave_error_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to delete leave")
            null
        }.`when`(repo).deleteLeave(any(), any(), any())

        viewModel.deleteLeave("leave123")

        val result = viewModel.operationResult.first()
        assertEquals(false, result?.first)
        assertEquals("Failed to delete leave", result?.second)

        verify(repo).deleteLeave(eq("doctor123"), eq("leave123"), any())
    }

    @Test
    fun clearOperationResult_test() = runBlocking {
        val repo = mock<LeaveManagementRepo>()
        val viewModel = LeaveManagementViewModel(repo, "doctor123")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Success")
            null
        }.`when`(repo).addLeave(any(), any())

        viewModel.addLeave("2024-01-15", "2024-01-20", "Vacation", "Planned")
        assertEquals(true, viewModel.operationResult.first()?.first)

        viewModel.clearOperationResult()
        assertNull(viewModel.operationResult.first())
    }
}