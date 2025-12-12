package com.example.medhomeapp.repository

import android.net.Uri
import com.example.medhomeapp.model.HealthRecordsModel
import android.content.Context
import com.example.medhomeapp.view.HealthRecords

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class HealthRecordsRepoImpl(private val context: Context): HealthRecordsRepo {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val userId get() = auth.currentUser?.uid?: ""

    private fun collectionRef() =
        firestore.collection("users")
            .document(userId)
            .collection("health_records")

    //This function saves file locally
    private fun saveFileLocally(fileUri: Uri): String{
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
        val documentId = collectionRef().document().id

        val filePath = fileUri?.let { saveFileLocally(it) }?: ""

        val finalRecord = record.copy(
            id = documentId,
            userId = userId,
            fileUrl = filePath,
            fileName = File(filePath).name
        )

        collectionRef().document(documentId).set(finalRecord)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getHealthRecords(
        onSuccess: (List<HealthRecordsModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        collectionRef()
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null){
                    onError(error)
                    return@addSnapshotListener
                }
                val records = snapshot?.toObjects(HealthRecordsModel::class.java)?:emptyList()
                onSuccess(records)
            }
    }

    override fun updateHealthRecord(
        record: HealthRecordsModel,
        fileUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        var updateRecord = record

        if (fileUri != null){
//            This deletes old file after update
            if(record.fileUrl.isNotEmpty()){
                File(record.fileUrl).delete()
            }
            //Saves the updated new file
            val newPath = saveFileLocally(fileUri)
            updateRecord = record.copy(
                fileUrl = newPath,
                fileName = File(newPath).name
            )
        }
        collectionRef().document(record.id)
            .set(updateRecord)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun deleteHealthRecord(
        recordId: String,
        fileUri: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
       if (fileUri.isNotEmpty()){
           File(fileUri).delete()
       }
        collectionRef().document(recordId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }


}