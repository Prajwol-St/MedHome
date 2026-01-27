package com.example.medhomeapp

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.HealthRecordsModel
import com.example.medhomeapp.repository.HealthRecordsRepo
import com.example.medhomeapp.viewmodel.HealthRecordsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class HealthRecordsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadHealthRecords_success_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val mockRecords = listOf(
            HealthRecordsModel(
                id = "1",
                title = "Blood Test",
                date = "2024-01-15",
                description = "Annual Checkup"
            ),
            HealthRecordsModel(
                id = "2",
                title = "X-ray",
                date = "2024-02-20",
                description = "Chest X-ray"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<HealthRecordsModel>) -> Unit>(0)
            onSuccess(mockRecords)
            null
        }.`when`(repo).getHealthRecords(any(), any())

        viewModel.loadHealthRecords()

        assertEquals(mockRecords, viewModel.healthRecords.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).getHealthRecords(any(), any())
    }

    @Test
    fun loadHealthRecords_error_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val errorMsg = "Failed to load records"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(1)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).getHealthRecords(any(), any())

        viewModel.loadHealthRecords()

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)

        verify(repo).getHealthRecords(any(), any())
    }

    @Test
    fun addHealthRecord_success_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val newRecord = HealthRecordsModel(
            id = "3",
            title = "MRI Scan",
            date = "2024-03-10",
            description = "Brain scan"
        )
        val fileUri = mock<Uri>()

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).addHealthRecord(eq(newRecord), eq(fileUri), any(), any())

        viewModel.addHealthRecord(newRecord, fileUri)

        assertEquals("Record added successfully", viewModel.successMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).addHealthRecord(eq(newRecord), eq(fileUri), any(), any())
    }

    @Test
    fun addHealthRecord_error_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val newRecord = HealthRecordsModel(
            id = "3",
            title = "MRI Scan",
            date = "2024-03-10",
            description = "Brain scan"
        )
        val errorMsg = "Failed to add record"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).addHealthRecord(eq(newRecord), eq(null), any(), any())

        viewModel.addHealthRecord(newRecord, null)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)

        verify(repo).addHealthRecord(eq(newRecord), eq(null), any(), any())
    }

    @Test
    fun updateHealthRecord_success_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val updatedRecord = HealthRecordsModel(
            id = "1",
            title = "Blood Test Updated",
            date = "2024-01-15",
            description = "Updated description"
        )
        val fileUri = mock<Uri>()

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).updateHealthRecord(eq(updatedRecord), eq(fileUri), any(), any())

        viewModel.updateHealthRecord(updatedRecord, fileUri)

        assertEquals("Record updated successfully", viewModel.successMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).updateHealthRecord(eq(updatedRecord), eq(fileUri), any(), any())
    }

    @Test
    fun updateHealthRecord_error_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val updatedRecord = HealthRecordsModel(
            id = "1",
            title = "Blood Test Updated",
            date = "2024-01-15",
            description = "Updated description"
        )
        val errorMsg = "Failed to update record"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).updateHealthRecord(eq(updatedRecord), eq(null), any(), any())

        viewModel.updateHealthRecord(updatedRecord, null)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)

        verify(repo).updateHealthRecord(eq(updatedRecord), eq(null), any(), any())
    }

    @Test
    fun deleteHealthRecord_success_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val recordId = "1"
        val publicId = "public123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).deleteHealthRecord(eq(recordId), eq(publicId), any(), any())

        viewModel.deleteHealthRecord(recordId, publicId)

        assertEquals("Record deleted successfully", viewModel.successMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        verify(repo).deleteHealthRecord(eq(recordId), eq(publicId), any(), any())
    }

    @Test
    fun deleteHealthRecord_error_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        val recordId = "1"
        val publicId = "public123"
        val errorMsg = "Failed to delete record"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).deleteHealthRecord(eq(recordId), eq(publicId), any(), any())

        viewModel.deleteHealthRecord(recordId, publicId)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)

        verify(repo).deleteHealthRecord(eq(recordId), eq(publicId), any(), any())
    }

    @Test
    fun clearMessages_test() {
        val repo = mock<HealthRecordsRepo>()
        val viewModel = HealthRecordsViewModel(repo)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).addHealthRecord(any(), any(), any(), any())

        val record = HealthRecordsModel(
            id = "1",
            title = "Test",
            date = "2024-01-01",
            description = "Test"
        )
        viewModel.addHealthRecord(record, null)

        assertEquals("Record added successfully", viewModel.successMessage.value)

        viewModel.clearMessages()

        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
    }
}