package com.example.medhomeapp.repository

import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel

class BloodDonationRepoImpl : BloodDonationRepo {
    override fun postBloodRequest(
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAllBloodRequests(
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getBloodRequestByGroup(
        bloodGroup: String,
        onSuccess: (List<BloodRequestModel>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getBloodRequestsByUserId(
        userId: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getBloodRequestById(
        requestId: String,
        onSuccess: (BloodRequestModel) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateBloodRequest(
        requestId: String,
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateBloodRequestStaus(
        requestId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteBloodRequest(
        requestId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun createOrUpdateDonorProfile(
        donorProfile: DonorModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getDonorProfile(
        userId: String,
        onSuccess: (DonorModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAllDonors(
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getDonorsByBloodGroup(
        bloodGroup: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateDonorAvailability(
        userId: String,
        isAvailable: Boolean,
        isEmergencyAvailable: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateLastDonationDate(
        userId: String,
        donationDate: Long,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteDonorProfile(
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun searchBloodRequestByLocation(
        location: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getUrgentBloodRequests(
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAvailableDonorsByLocation(
        location: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserId(): String? {
        TODO("Not yet implemented")
    }
}