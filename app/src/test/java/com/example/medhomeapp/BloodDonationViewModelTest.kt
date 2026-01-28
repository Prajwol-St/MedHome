package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel
import com.example.medhomeapp.repository.BloodDonationRepo
import com.example.medhomeapp.viewmodel.BloodDonationViewModel
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
import org.mockito.kotlin.whenever

class BloodDonationViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: BloodDonationRepo
    private lateinit var viewModel: BloodDonationViewModel

    @Before
    fun setup() {
        repo = mock()
        viewModel = BloodDonationViewModel(repo)
    }

    @Test
    fun getCurrentUserId_returnsUserId_test() {
        val userId = "user123"
        whenever(repo.getCurrentUserId()).thenReturn(userId)

        val result = viewModel.getCurrentUserId()

        assertEquals(userId, result)
        verify(repo).getCurrentUserId()
    }

    @Test
    fun postBloodRequest_withEmptyFields_setsError_test() = runBlocking {
        viewModel.postBloodRequest(
            patientName = "John Doe",
            bloodGroup = "",
            unitsNeeded = "2",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgencyLevel = "Urgent",
            additionalNotes = "None"
        )

        val error = viewModel.error.first()
        assertEquals("Please fill all required fields", error)
    }

    @Test
    fun postBloodRequest_withEmptyBloodGroup_setsError_test() = runBlocking {
        viewModel.postBloodRequest(
            patientName = "John Doe",
            bloodGroup = "",
            unitsNeeded = "2",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgencyLevel = "Urgent",
            additionalNotes = "Emergency"
        )

        val error = viewModel.error.first()
        assertEquals("Please fill all required fields", error)
    }

    @Test
    fun postBloodRequest_withEmptyUnitsNeeded_setsError_test() = runBlocking {
        viewModel.postBloodRequest(
            patientName = "John Doe",
            bloodGroup = "A+",
            unitsNeeded = "",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgencyLevel = "Urgent",
            additionalNotes = "Emergency"
        )

        val error = viewModel.error.first()
        assertEquals("Please fill all required fields", error)
    }

    @Test
    fun postBloodRequest_success_test() = runBlocking {
        val bloodRequest = BloodRequestModel(
            patientName = "John Doe",
            bloodGroup = "A+",
            unitsNeeded = "2",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgency = "Urgent",
            additionalNotes = "Emergency"
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(repo).postBloodRequest(any(), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(listOf(bloodRequest))
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.postBloodRequest(
            patientName = "John Doe",
            bloodGroup = "A+",
            unitsNeeded = "2",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgencyLevel = "Urgent",
            additionalNotes = "Emergency"
        )

        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Blood request posted successfully", successMessage)

        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }

    @Test
    fun postBloodRequest_error_test() = runBlocking {
        val errorMsg = "Failed to post blood request"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).postBloodRequest(any(), any(), any())

        viewModel.postBloodRequest(
            patientName = "John Doe",
            bloodGroup = "A+",
            unitsNeeded = "2",
            hospital = "City Hospital",
            location = "Downtown",
            contactNumber = "1234567890",
            urgencyLevel = "Urgent",
            additionalNotes = "Emergency"
        )

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun getAllBloodRequests_success_test() = runBlocking {
        val mockRequests = listOf(
            BloodRequestModel(
                id = "1",
                patientName = "Patient 1",
                bloodGroup = "A+",
                unitsNeeded = "2",
                hospital = "Hospital A",
                location = "Location A",
                contactNumber = "1111111111",
                urgency = "Urgent",
                additionalNotes = "Notes A"
            ),
            BloodRequestModel(
                id = "2",
                patientName = "Patient 2",
                bloodGroup = "B+",
                unitsNeeded = "3",
                hospital = "Hospital B",
                location = "Location B",
                contactNumber = "2222222222",
                urgency = "Normal",
                additionalNotes = "Notes B"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(mockRequests)
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.getAllBloodRequests()

        // Wait a bit for async operations
        Thread.sleep(100)

        val requests = viewModel.bloodRequests.first()
        assertEquals(mockRequests, requests)

        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }

    @Test
    fun getAllBloodRequests_error_test() = runBlocking {
        val errorMsg = "Failed to load blood requests"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(1)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.getAllBloodRequests()

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun getBloodRequestsByGroup_success_test() = runBlocking {
        val bloodGroup = "A+"
        val mockRequests = listOf(
            BloodRequestModel(
                id = "1",
                patientName = "Patient 1",
                bloodGroup = bloodGroup,
                unitsNeeded = "2",
                hospital = "Hospital A",
                location = "Location A",
                contactNumber = "1111111111",
                urgency = "Urgent",
                additionalNotes = "Notes A"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(1)
            onSuccess(mockRequests)
            null
        }.`when`(repo).getBloodRequestByGroup(eq(bloodGroup), any(), any())

        viewModel.getBloodRequestsByGroup(bloodGroup)

        // Wait a bit for async operations
        Thread.sleep(100)

        val requests = viewModel.bloodRequests.first()
        assertEquals(mockRequests, requests)
    }

    @Test
    fun updateBloodRequestStatus_success_test() = runBlocking {
        val requestId = "request123"
        val status = "fulfilled"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).updateBloodRequestStaus(eq(requestId), eq(status), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.updateBloodRequestStatus(requestId, status)

        // Wait a bit for async operations
        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Status updated successfully", successMessage)
    }

    @Test
    fun updateBloodRequestStatus_error_test() = runBlocking {
        val requestId = "request123"
        val status = "fulfilled"
        val errorMsg = "Failed to update status"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).updateBloodRequestStaus(eq(requestId), eq(status), any(), any())

        viewModel.updateBloodRequestStatus(requestId, status)

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun deleteBloodRequest_success_test() = runBlocking {
        val requestId = "request123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(repo).deleteBloodRequest(eq(requestId), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.deleteBloodRequest(requestId)

        // Wait a bit for async operations
        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Blood request deleted successfully", successMessage)
    }

    @Test
    fun deleteBloodRequest_error_test() = runBlocking {
        val requestId = "request123"
        val errorMsg = "Failed to delete blood request"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).deleteBloodRequest(eq(requestId), any(), any())

        viewModel.deleteBloodRequest(requestId)

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun createOrUpdateDonorProfile_withEmptyBloodGroup_setsError_test() = runBlocking {
        viewModel.createOrUpdateDonorProfile(
            userName = "John",
            bloodGroup = "",
            isAvailable = true,
            isEmergencyAvailable = true,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        val error = viewModel.error.first()
        assertEquals("Please select your blood group", error)
    }

    @Test
    fun createOrUpdateDonorProfile_success_test() = runBlocking {
        val userId = "user123"
        val donorProfile = DonorModel(
            userId = userId,
            userName = "John",
            bloodGroup = "A+",
            isAvailable = true,
            isEmergencyAvailable = true,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(repo).createOrUpdateDonorProfile(any(), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DonorModel?) -> Unit>(1)
            onSuccess(donorProfile)
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        viewModel.createOrUpdateDonorProfile(
            userName = "John",
            bloodGroup = "A+",
            isAvailable = true,
            isEmergencyAvailable = true,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        // Wait a bit for async operations
        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Donor profile saved successfully", successMessage)
    }

    @Test
    fun createOrUpdateDonorProfile_error_test() = runBlocking {
        val errorMsg = "Failed to save donor profile"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).createOrUpdateDonorProfile(any(), any(), any())

        viewModel.createOrUpdateDonorProfile(
            userName = "John",
            bloodGroup = "A+",
            isAvailable = true,
            isEmergencyAvailable = true,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun loadDonorProfile_success_test() = runBlocking {
        val userId = "user123"
        val donorProfile = DonorModel(
            userId = userId,
            userName = "John",
            bloodGroup = "A+",
            isAvailable = true,
            isEmergencyAvailable = false,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DonorModel?) -> Unit>(1)
            onSuccess(donorProfile)
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        viewModel.loadDonorProfile()

        // Wait a bit for async operations
        Thread.sleep(100)

        val profile = viewModel.donorProfile.first()
        assertEquals(donorProfile, profile)
    }

    @Test
    fun loadDonorProfile_error_test() = runBlocking {
        val userId = "user123"
        val errorMsg = "Failed to load donor profile"

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        viewModel.loadDonorProfile()

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun getAllDonors_success_test() = runBlocking {
        val mockDonors = listOf(
            DonorModel(
                userId = "user1",
                userName = "Donor 1",
                bloodGroup = "A+",
                isAvailable = true,
                isEmergencyAvailable = true,
                contactNumber = "1111111111",
                location = "Location 1"
            ),
            DonorModel(
                userId = "user2",
                userName = "Donor 2",
                bloodGroup = "B+",
                isAvailable = false,
                isEmergencyAvailable = false,
                contactNumber = "2222222222",
                location = "Location 2"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<DonorModel>) -> Unit>(0)
            onSuccess(mockDonors)
            null
        }.`when`(repo).getAllDonors(any(), any())

        viewModel.getAllDonors()

        // Wait a bit for async operations
        Thread.sleep(100)

        val donors = viewModel.donors.first()
        assertEquals(mockDonors, donors)
    }

    @Test
    fun getAllDonors_error_test() = runBlocking {
        val errorMsg = "Failed to load donors"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(1)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).getAllDonors(any(), any())

        viewModel.getAllDonors()

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun getDonorsByBloodGroup_success_test() = runBlocking {
        val bloodGroup = "A+"
        val mockDonors = listOf(
            DonorModel(
                userId = "user1",
                userName = "Donor 1",
                bloodGroup = bloodGroup,
                isAvailable = true,
                isEmergencyAvailable = true,
                contactNumber = "1111111111",
                location = "Location 1"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<DonorModel>) -> Unit>(1)
            onSuccess(mockDonors)
            null
        }.`when`(repo).getDonorsByBloodGroup(eq(bloodGroup), any(), any())

        viewModel.getDonorsByBloodGroup(bloodGroup)

        // Wait a bit for async operations
        Thread.sleep(100)

        val donors = viewModel.donors.first()
        assertEquals(mockDonors, donors)
    }

    @Test
    fun updateDonorAvailability_success_test() = runBlocking {
        val userId = "user123"
        val isAvailable = true
        val isEmergencyAvailable = false

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(3)
            onSuccess()
            null
        }.`when`(repo).updateDonorAvailability(eq(userId), eq(isAvailable), eq(isEmergencyAvailable), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DonorModel?) -> Unit>(1)
            onSuccess(null)
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        viewModel.updateDonorAvailability(isAvailable, isEmergencyAvailable)

        // Wait a bit for async operations
        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Availability updated successfully", successMessage)
    }

    @Test
    fun updateDonorAvailability_error_test() = runBlocking {
        val userId = "user123"
        val errorMsg = "Failed to update availability"

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(4)
            onError(Exception(errorMsg))
            null
        }.`when`(repo).updateDonorAvailability(eq(userId), any(), any(), any(), any())

        viewModel.updateDonorAvailability(true, false)

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals(errorMsg, error)
    }

    @Test
    fun getUserBloodRequests_withNoUserId_setsError_test() = runBlocking {
        whenever(repo.getCurrentUserId()).thenReturn(null)

        viewModel.getUserBloodRequests()

        // Wait a bit for async operations
        Thread.sleep(100)

        val error = viewModel.error.first()
        assertEquals("User not authenticated", error)
    }

    @Test
    fun getUserBloodRequests_success_test() = runBlocking {
        val userId = "user123"
        val mockRequests = listOf(
            BloodRequestModel(
                id = "1",
                userId = userId,
                patientName = "Patient 1",
                bloodGroup = "A+",
                unitsNeeded = "2",
                hospital = "Hospital A",
                location = "Location A",
                contactNumber = "1111111111",
                urgency = "Urgent",
                additionalNotes = "Notes A"
            )
        )

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(1)
            onSuccess(mockRequests)
            null
        }.`when`(repo).getBloodRequestsByUserId(eq(userId), any(), any())

        viewModel.getUserBloodRequests()

        // Wait a bit for async operations
        Thread.sleep(100)

        val requests = viewModel.bloodRequests.first()
        assertEquals(mockRequests, requests)
    }

    @Test
    fun updateLastDonationDate_success_test() = runBlocking {
        val userId = "user123"
        val donationDate = System.currentTimeMillis()
        val currentProfile = DonorModel(
            userId = userId,
            userName = "John",
            bloodGroup = "A+",
            isAvailable = true,
            isEmergencyAvailable = false,
            contactNumber = "1234567890",
            location = "Downtown"
        )

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        // Set initial profile
        viewModel.clearAllState()

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DonorModel?) -> Unit>(1)
            onSuccess(currentProfile)
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        viewModel.loadDonorProfile()
        Thread.sleep(100)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(repo).createOrUpdateDonorProfile(any(), any(), any())

        viewModel.updateLastDonationDate(donationDate)

        // Wait a bit for async operations
        Thread.sleep(100)

        val successMessage = viewModel.successMessage.first()
        assertEquals("Donation date updated successfully", successMessage)
    }

    @Test
    fun markRequestAsFulfilled_callsUpdateStatus_test() = runBlocking {
        val requestId = "request123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).updateBloodRequestStaus(eq(requestId), eq("fulfilled"), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.markRequestAsFulfilled(requestId)

        // Wait a bit for async operations
        Thread.sleep(100)

        verify(repo).updateBloodRequestStaus(eq(requestId), eq("fulfilled"), any(), any())
    }

    @Test
    fun cancelBloodRequest_callsUpdateStatus_test() = runBlocking {
        val requestId = "request123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).updateBloodRequestStaus(eq(requestId), eq("cancelled"), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.cancelBloodRequest(requestId)

        // Wait a bit for async operations
        Thread.sleep(100)

        verify(repo).updateBloodRequestStaus(eq(requestId), eq("cancelled"), any(), any())
    }

    @Test
    fun reactivateBloodRequest_callsUpdateStatus_test() = runBlocking {
        val requestId = "request123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(repo).updateBloodRequestStaus(eq(requestId), eq("active"), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.reactivateBloodRequest(requestId)

        // Wait a bit for async operations
        Thread.sleep(100)

        verify(repo).updateBloodRequestStaus(eq(requestId), eq("active"), any(), any())
    }

    @Test
    fun getCompatibleDonors_forAPositive_filtersCorrectly_test() = runBlocking {
        val allDonors = listOf(
            DonorModel(userId = "1", userName = "D1", bloodGroup = "A+", isAvailable = true, isEmergencyAvailable = false, contactNumber = "111", location = "L1"),
            DonorModel(userId = "2", userName = "D2", bloodGroup = "A-", isAvailable = true, isEmergencyAvailable = false, contactNumber = "222", location = "L2"),
            DonorModel(userId = "3", userName = "D3", bloodGroup = "O+", isAvailable = true, isEmergencyAvailable = false, contactNumber = "333", location = "L3"),
            DonorModel(userId = "4", userName = "D4", bloodGroup = "O-", isAvailable = true, isEmergencyAvailable = false, contactNumber = "444", location = "L4"),
            DonorModel(userId = "5", userName = "D5", bloodGroup = "B+", isAvailable = true, isEmergencyAvailable = false, contactNumber = "555", location = "L5")
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<DonorModel>) -> Unit>(0)
            onSuccess(allDonors)
            null
        }.`when`(repo).getAllDonors(any(), any())

        viewModel.getAllDonors()
        Thread.sleep(100)

        viewModel.getCompatibleDonors("A+")

        val compatibleDonors = viewModel.donors.first()
        assertEquals(4, compatibleDonors.size)
        assertTrue(compatibleDonors.any { it.bloodGroup == "A+" })
        assertTrue(compatibleDonors.any { it.bloodGroup == "A-" })
        assertTrue(compatibleDonors.any { it.bloodGroup == "O+" })
        assertTrue(compatibleDonors.any { it.bloodGroup == "O-" })
    }

    @Test
    fun getCompatibleDonors_forONegative_filtersCorrectly_test() = runBlocking {
        val allDonors = listOf(
            DonorModel(userId = "1", userName = "D1", bloodGroup = "A+", isAvailable = true, isEmergencyAvailable = false, contactNumber = "111", location = "L1"),
            DonorModel(userId = "2", userName = "D2", bloodGroup = "O-", isAvailable = true, isEmergencyAvailable = false, contactNumber = "222", location = "L2"),
            DonorModel(userId = "3", userName = "D3", bloodGroup = "O+", isAvailable = true, isEmergencyAvailable = false, contactNumber = "333", location = "L3")
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<DonorModel>) -> Unit>(0)
            onSuccess(allDonors)
            null
        }.`when`(repo).getAllDonors(any(), any())

        viewModel.getAllDonors()
        Thread.sleep(100)

        viewModel.getCompatibleDonors("O-")

        val compatibleDonors = viewModel.donors.first()
        assertEquals(1, compatibleDonors.size)
        assertEquals("O-", compatibleDonors[0].bloodGroup)
    }

    @Test
    fun clearError_clearsErrorState_test() = runBlocking {
        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(1)
            onError(Exception("Some error"))
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.getAllBloodRequests()
        Thread.sleep(100)

        viewModel.clearError()

        val error = viewModel.error.first()
        assertNull(error)
    }

    @Test
    fun clearSuccessMessage_clearsSuccessState_test() = runBlocking {
        val requestId = "request123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(repo).deleteBloodRequest(eq(requestId), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        viewModel.deleteBloodRequest(requestId)
        Thread.sleep(100)

        viewModel.clearSuccessMessage()

        val successMessage = viewModel.successMessage.first()
        assertNull(successMessage)
    }

    @Test
    fun clearAllState_resetsAllValues_test() = runBlocking {
        viewModel.clearAllState()

        val bloodRequests = viewModel.bloodRequests.first()
        assertTrue(bloodRequests.isEmpty())

        val donorProfile = viewModel.donorProfile.first()
        assertNull(donorProfile)

        val donors = viewModel.donors.first()
        assertTrue(donors.isEmpty())

        val error = viewModel.error.first()
        assertNull(error)

        val successMessage = viewModel.successMessage.first()
        assertNull(successMessage)

        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }

    @Test
    fun refreshAllData_callsAllLoadMethods_test() = runBlocking {
        val userId = "user123"

        whenever(repo.getCurrentUserId()).thenReturn(userId)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<BloodRequestModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllBloodRequests(any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DonorModel?) -> Unit>(1)
            onSuccess(null)
            null
        }.`when`(repo).getDonorProfile(eq(userId), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<DonorModel>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(repo).getAllDonors(any(), any())

        viewModel.refreshAllData()

        Thread.sleep(200)

        verify(repo).getAllBloodRequests(any(), any())
        verify(repo).getDonorProfile(eq(userId), any(), any())
        verify(repo).getAllDonors(any(), any())
    }
}