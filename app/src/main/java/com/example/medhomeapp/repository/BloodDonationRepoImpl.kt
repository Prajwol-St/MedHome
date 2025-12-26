package com.example.medhomeapp.repository

import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BloodDonationRepoImpl : BloodDonationRepo {

    private val database = FirebaseDatabase.getInstance()
    private val bloodRequestRef = database.getReference("bloodRequests")
    private val donorsRef = database.getReference("donors")
    private val auth = FirebaseAuth.getInstance()
    override fun postBloodRequest(
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val requestId = bloodRequestRef.push().key ?: return
        onError(Exception("Failed to generate ID"))
        val userId = getCurrentUserId() ?: return onError(Exception("User not authenticated"))

        val requestWithId = bloodRequest.copy(
            id = requestId,
            userId = userId,
            timestamp = System.currentTimeMillis(),
            status = "active"
        )

        bloodRequestRef.child(requestId).setValue(requestWithId.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getAllBloodRequests(
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requests = mutableListOf<BloodRequestModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(BloodRequestModel::class.java)?.let {
                            requests.add(it)
                        }
                    }
                    onSuccess(requests.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getBloodRequestByGroup(
        bloodGroup: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (bloodGroup == "All"){
            getAllBloodRequests(onSuccess, onError)
            return
        }
        bloodRequestRef.orderByChild("bloodGroup").equalTo(bloodGroup)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requests = mutableListOf<BloodRequestModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(BloodRequestModel::class.java)?.let {
                            requests.add(it)
                        }
                    }
                    onSuccess(requests.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                   onError(error.toException())
                }

            })
    }

    override fun getBloodRequestsByUserId(
        userId: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requests = mutableListOf<BloodRequestModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(BloodRequestModel::class.java)?.let {
                            requests.add(it)
                        }
                    }
                    onSuccess(requests.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }

            })
    }

    override fun getBloodRequestById(
        requestId: String,
        onSuccess: (BloodRequestModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.child(requestId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val request = snapshot.getValue(BloodRequestModel::class.java)
                onSuccess(request)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }

        })
    }

    override fun updateBloodRequest(
        requestId: String,
        bloodRequest: BloodRequestModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val updateRequest = bloodRequest.copy(
            id = requestId,
            timestamp = System.currentTimeMillis()
        )
        bloodRequestRef.child(requestId).setValue(updateRequest.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun updateBloodRequestStaus(
        requestId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
       bloodRequestRef.child(requestId).child("statys").setValue((status))
           .addOnSuccessListener { onSuccess() }
           .addOnFailureListener { onError(it) }
    }

    override fun deleteBloodRequest(
        requestId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.child(requestId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun createOrUpdateDonorProfile(
        donorProfile: DonorModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = getCurrentUserId() ?: return onError(Exception("User not authenticated"))

        val profileWithId = donorProfile.copy(
            id = userId,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )

        donorsRef.child(userId).setValue(profileWithId.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getDonorProfile(
        userId: String,
        onSuccess: (DonorModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        donorsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(DonorModel::class.java)
                onSuccess(profile)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }

        })
    }

    override fun getAllDonors(
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        donorsRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val donors = mutableListOf<DonorModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(DonorModel::class.java)?.let{
                            donors.add(it)
                        }
                    }
                    onSuccess(donors.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }

            })
    }

    override fun getDonorsByBloodGroup(
        bloodGroup: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        donorsRef.orderByChild("bloodGroup").equalTo(bloodGroup)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val donors = mutableListOf<DonorModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(DonorModel::class.java)?.let {
                            if (it.isAvailable){
                                donors.add(it)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }

            })
    }

    override fun updateDonorAvailability(
        userId: String,
        isAvailable: Boolean,
        isEmergencyAvailable: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val updates = mapOf(
            "isAvailable" to isAvailable,
            "isEmergencyAvailable" to isEmergencyAvailable
        )

        donorsRef.child(userId).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun updateLastDonationDate(
        userId: String,
        donationDate: Long,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        donorsRef.child(userId).child("lastDonationDate").setValue(donationDate)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun deleteDonorProfile(
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        donorsRef.child(userId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun searchBloodRequestByLocation(
        location: String,
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.orderByChild("status").equalTo("active")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requests = mutableListOf<BloodRequestModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(BloodRequestModel::class.java)?.let { request ->
                            if (request.location.contains(location, ignoreCase = true)){
                                requests.add(request)
                            }
                        }
                    }
                    onSuccess(requests.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }

            })
    }

    override fun getUrgentBloodRequests(
        onSuccess: (List<BloodRequestModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        bloodRequestRef.orderByChild("urgency").equalTo("Urgent")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val requests = mutableListOf<BloodRequestModel>()
                    for (childSnapshot in snapshot.children){
                        childSnapshot.getValue(BloodRequestModel::class.java)?.let { request ->
                            if (request.status == "active"){
                                requests.add(request)
                            }
                        }
                    }
                    onSuccess(requests.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }

            })
    }

    override fun getAvailableDonorsByLocation(
        location: String,
        onSuccess: (List<DonorModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}