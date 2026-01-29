package com.example.medhomeapp.repository

import android.net.Uri
import com.example.medhomeapp.model.HealthRecordsModel
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HealthRecordsRepoImpl(
    private val context: Context,
    private val commonRepo: CommonRepo = CommonRepoImpl() // ADDED: Cloudinary repo dependency
): HealthRecordsRepo {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val userId get() = auth.currentUser?.uid ?: ""

    private fun collectionRef() = database.getReference("health_records")

    override fun addHealthRecord(
        record: HealthRecordsModel,
        fileUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        android.util.Log.d("HealthRecordsRepo", "=== ADD RECORD ===")
        android.util.Log.d("HealthRecordsRepo", "UserId: $userId")

        if (userId.isEmpty()) {
            onError(Exception("User not authenticated"))
            return
        }

        val recordId = collectionRef().push().key ?: ""
        if (fileUri != null) {
            commonRepo.uploadImage(
                context = context,
                imageUri = fileUri,
                folder = "health_records",
                callback = { success, message, imageUrl, publicId ->
                    if (success && imageUrl != null && publicId != null) {
                        saveRecordToFirebase(
                            record = record,
                            recordId = recordId,
                            fileUrl = imageUrl,
                            publicId = publicId,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    } else {
                        android.util.Log.e("HealthRecordsRepo", "Upload failed: $message")
                        onError(Exception(message))
                    }
                }
            )
        } else {
            saveRecordToFirebase(
                record = record,
                recordId = recordId,
                fileUrl = "",
                publicId = "",
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    private fun saveRecordToFirebase(
        record: HealthRecordsModel,
        recordId: String,
        fileUrl: String,
        publicId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val fileName = if (fileUrl.isNotEmpty()) {
            fileUrl.substringAfterLast("/").substringBefore(".")
        } else ""

        val finalRecord = record.copy(
            id = recordId,
            userId = userId,
            fileUrl = fileUrl,
            publicId = publicId,
            fileName = fileName,
            timestamp = System.currentTimeMillis()
        )

        android.util.Log.d("HealthRecordsRepo", "Saving record: ${finalRecord.title}")

        collectionRef().child(recordId).setValue(finalRecord)
            .addOnSuccessListener {
                android.util.Log.d("HealthRecordsRepo", "Record saved successfully")
                onSuccess()
            }
            .addOnFailureListener {
                android.util.Log.e("HealthRecordsRepo", "Save failed: ${it.message}")
                onError(it)
            }
    }

    override fun getHealthRecords(
        onSuccess: (List<HealthRecordsModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        android.util.Log.d("HealthRecordsRepo", "=== GET RECORDS ===")
        android.util.Log.d("HealthRecordsRepo", "UserId: $userId")

        if (userId.isEmpty()) {
            android.util.Log.e("HealthRecordsRepo", "User not authenticated")
            onError(Exception("User not authenticated"))
            return
        }

        collectionRef()
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    android.util.Log.d("HealthRecordsRepo", "onDataChange - exists: ${snapshot.exists()}")
                    android.util.Log.d("HealthRecordsRepo", "Children count: ${snapshot.childrenCount}")

                    val records = mutableListOf<HealthRecordsModel>()

                    for (childSnapshot in snapshot.children) {
                        android.util.Log.d("HealthRecordsRepo", "Child key: ${childSnapshot.key}")
                        val record = childSnapshot.getValue(HealthRecordsModel::class.java)
                        if (record != null) {
                            android.util.Log.d("HealthRecordsRepo", "Record: ${record.title}")
                            records.add(record)
                        }
                    }

                    records.sortByDescending { it.timestamp }
                    android.util.Log.d("HealthRecordsRepo", "Total records: ${records.size}")
                    onSuccess(records)
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("HealthRecordsRepo", "Error: ${error.message}")
                    onError(error.toException())
                }
            })
    }

    override fun updateHealthRecord(
        record: HealthRecordsModel,
        fileUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (userId.isEmpty()) {
            onError(Exception("User not authenticated"))
            return
        }

        if (fileUri != null) {
            if (record.publicId.isNotEmpty()) {
                commonRepo.deleteImage(record.publicId) { success, message ->
                    android.util.Log.d("HealthRecordsRepo", "Old image deletion: $success - $message")
                }
            }

            commonRepo.uploadImage(
                context = context,
                imageUri = fileUri,
                folder = "health_records",
                callback = { success, message, imageUrl, publicId ->
                    if (success && imageUrl != null && publicId != null) {
                        val fileName = imageUrl.substringAfterLast("/").substringBefore(".")
                        val updatedRecord = record.copy(
                            fileUrl = imageUrl,
                            publicId = publicId,
                            fileName = fileName
                        )
                        updateRecordInFirebase(updatedRecord, onSuccess, onError)
                    } else {
                        android.util.Log.e("HealthRecordsRepo", "Upload failed: $message")
                        onError(Exception(message))
                    }
                }
            )
        } else {
            updateRecordInFirebase(record, onSuccess, onError)
        }
    }

    private fun updateRecordInFirebase(
        record: HealthRecordsModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        collectionRef().child(record.id)
            .setValue(record)
            .addOnSuccessListener {
                android.util.Log.d("HealthRecordsRepo", "Record updated successfully")
                onSuccess()
            }
            .addOnFailureListener {
                android.util.Log.e("HealthRecordsRepo", "Update failed: ${it.message}")
                onError(it)
            }
    }

    override fun deleteHealthRecord(
        recordId: String,
        publicId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (userId.isEmpty()) {
            onError(Exception("User not authenticated"))
            return
        }

        if (publicId.isNotEmpty()) {
            commonRepo.deleteImage(publicId) { success, message ->
                android.util.Log.d("HealthRecordsRepo", "Image deletion: $success - $message")
                // Continue with Firebase deletion even if Cloudinary deletion fails
                deleteRecordFromFirebase(recordId, onSuccess, onError)
            }
        } else {
            deleteRecordFromFirebase(recordId, onSuccess, onError)
        }
    }

    private fun deleteRecordFromFirebase(
        recordId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        collectionRef().child(recordId)
            .removeValue()
            .addOnSuccessListener {
                android.util.Log.d("HealthRecordsRepo", "Record deleted successfully")
                onSuccess()
            }
            .addOnFailureListener {
                android.util.Log.e("HealthRecordsRepo", "Delete failed: ${it.message}")
                onError(it)
            }
    }
}