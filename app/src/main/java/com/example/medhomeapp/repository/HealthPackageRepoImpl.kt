package com.example.medhomeapp.repository

import com.example.medhomeapp.model.HealthPackage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HealthPackageRepoImpl : HealthPackageRepo {

    private val database = FirebaseDatabase.getInstance()
    private val packagesRef = database.getReference("health_packages")

    override fun createPackage(
        healthPackage: HealthPackage,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val packageId = packagesRef.push().key
        if (packageId == null) {
            onError(Exception("Failed to generate package ID"))
            return
        }

        val packageWithId = healthPackage.copy(
            id = packageId,
            timestamp = System.currentTimeMillis()
        )

        packagesRef.child(packageId).setValue(packageWithId.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getAllPackages(
        onSuccess: (List<HealthPackage>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        packagesRef.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val packages = mutableListOf<HealthPackage>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(HealthPackage::class.java)?.let {
                            packages.add(it)
                        }
                    }
                    onSuccess(packages.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getActivePackages(
        onSuccess: (List<HealthPackage>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        packagesRef.orderByChild("isActive").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val packages = mutableListOf<HealthPackage>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(HealthPackage::class.java)?.let {
                            packages.add(it)
                        }
                    }
                    onSuccess(packages.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getPackageById(
        packageId: String,
        onSuccess: (HealthPackage?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        packagesRef.child(packageId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val healthPackage = snapshot.getValue(HealthPackage::class.java)
                onSuccess(healthPackage)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    override fun updatePackage(
        packageId: String,
        healthPackage: HealthPackage,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val updatePackage = healthPackage.copy(
            id = packageId,
            timestamp = System.currentTimeMillis()
        )

        packagesRef.child(packageId).setValue(updatePackage.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun deletePackage(
        packageId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        packagesRef.child(packageId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun updatePackageStatus(
        packageId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        packagesRef.child(packageId).child("isActive").setValue(isActive)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}