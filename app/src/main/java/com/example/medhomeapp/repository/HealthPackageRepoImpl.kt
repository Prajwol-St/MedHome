package com.example.medhomeapp.repository

import com.example.medhomeapp.model.HealthPackageModel
import com.google.firebase.database.*

class HealthPackageRepoImpl : HealthPackageRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("HealthPackages")

    override fun createPackage(
        packageModel: HealthPackageModel,
        callback: (Boolean, String) -> Unit
    ) {
        val packageId = ref.push().key ?: return callback(false, "Failed to generate ID")
        val packageWithId = packageModel.copy(id = packageId)

        ref.child(packageId).setValue(packageWithId.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Package created successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to create package")
                }
            }
    }

    override fun getAllPackages(
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val packages = mutableListOf<HealthPackageModel>()
                    for (data in snapshot.children) {
                        val pkg = data.getValue(HealthPackageModel::class.java)
                        if (pkg != null) {
                            packages.add(pkg)
                        }
                    }
                    callback(true, "Packages fetched successfully", packages)
                } else {
                    callback(true, "No packages found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getPackagesByDoctor(
        doctorId: String,
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    ) {
        ref.orderByChild("doctorId").equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val packages = mutableListOf<HealthPackageModel>()
                        for (data in snapshot.children) {
                            val pkg = data.getValue(HealthPackageModel::class.java)
                            if (pkg != null) {
                                packages.add(pkg)
                            }
                        }
                        callback(true, "Doctor packages fetched", packages)
                    } else {
                        callback(true, "No packages found", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getActivePackages(
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    ) {
        ref.orderByChild("isActive").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val packages = mutableListOf<HealthPackageModel>()
                        for (data in snapshot.children) {
                            val pkg = data.getValue(HealthPackageModel::class.java)
                            if (pkg != null) {
                                packages.add(pkg)
                            }
                        }
                        callback(true, "Active packages fetched", packages)
                    } else {
                        callback(true, "No active packages", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getPackageById(
        packageId: String,
        callback: (Boolean, String, HealthPackageModel?) -> Unit
    ) {
        ref.child(packageId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val pkg = snapshot.getValue(HealthPackageModel::class.java)
                    if (pkg != null) {
                        callback(true, "Package fetched", pkg)
                    } else {
                        callback(false, "Failed to parse package", null)
                    }
                } else {
                    callback(false, "Package not found", null)
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to fetch", null)
            }
    }

    override fun updatePackage(
        packageId: String,
        packageModel: HealthPackageModel,
        callback: (Boolean, String) -> Unit
    ) {
        // 🔧 FIX: Use setValue instead of updateChildren to properly update all fields
        ref.child(packageId).setValue(packageModel.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Package updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update")
                }
            }
    }

    override fun deletePackage(
        packageId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(packageId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Package deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete")
                }
            }
    }

    override fun getPackagesByCategory(
        category: String,
        callback: (Boolean, String, List<HealthPackageModel>) -> Unit
    ) {
        ref.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val packages = mutableListOf<HealthPackageModel>()
                        for (data in snapshot.children) {
                            val pkg = data.getValue(HealthPackageModel::class.java)
                            if (pkg != null && pkg.isActive) {
                                packages.add(pkg)
                            }
                        }
                        callback(true, "Category packages fetched", packages)
                    } else {
                        callback(true, "No packages in category", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }
}