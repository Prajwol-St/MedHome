package com.example.medhomeapp.repository

import android.net.Uri
import com.example.medhomeapp.model.HealthRecordsModel
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream

class HealthRecordsRepoImpl(private val context: Context): HealthRecordsRepo {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val userId get() = auth.currentUser?.uid ?: ""

    // FLAT STRUCTURE - points to root health_records
    private fun collectionRef() =
        database.getReference("health_records")

    private fun saveFileLocally(fileUri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val fileName = "${System.currentTimeMillis()}_${fileUri.lastPathSegment}"
        val file = File(context.filesDir, fileName)

        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file.absolutePath
    }

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

        try {
            val recordId = collectionRef().push().key ?: ""
            val filePath = fileUri?.let { saveFileLocally(it) } ?: ""

            val finalRecord = record.copy(
                id = recordId,
                userId = userId,
                fileUrl = filePath,
                fileName = if (filePath.isNotEmpty()) File(filePath).name else "",
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
        } catch (e: Exception) {
            android.util.Log.e("HealthRecordsRepo", "Exception: ${e.message}")
            onError(e)
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

        // Query only records belonging to current user
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

        try {
            var updateRecord = record

            if (fileUri != null) {
                if (record.fileUrl.isNotEmpty()) {
                    File(record.fileUrl).delete()
                }
                val newPath = saveFileLocally(fileUri)
                updateRecord = record.copy(
                    fileUrl = newPath,
                    fileName = File(newPath).name
                )
            }

            collectionRef().child(record.id)
                .setValue(updateRecord)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun deleteHealthRecord(
        recordId: String,
        fileUrl: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (userId.isEmpty()) {
            onError(Exception("User not authenticated"))
            return
        }

        try {
            if (fileUrl.isNotEmpty()) {
                File(fileUrl).delete()
            }

            collectionRef().child(recordId)
                .removeValue()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        } catch (e: Exception) {
            onError(e)
        }
    }
}