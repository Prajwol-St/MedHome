package com.example.medhomeapp.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

class CommonRepoImpl : CommonRepo {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dyf4p475t",
            "api_key" to "151634777139866",
            "api_secret" to "x2FXlAtpjKfFZngdE8o6tvcZXxs"
        )
    )

    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String,
        callback: (Boolean, String, String?, String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)

                if (inputStream == null) {
                    Handler(Looper.getMainLooper()).post {
                        callback(false, "Failed to read image", null, null)
                    }
                    return@execute
                }

                var fileName = getFileNameFromUri(context, imageUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image_${System.currentTimeMillis()}"

                Log.d("CloudinaryUpload", "Starting upload: $fileName to folder: $folder")

                val response = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap(
                        "public_id", fileName,
                        "folder", folder,
                        "resource_type", "image",
                        "overwrite", true
                    )
                )

                var imageUrl = response["secure_url"] as? String
                val publicId = response["public_id"] as? String


                imageUrl = imageUrl?.replace("http://", "https://")

                Log.d("CloudinaryUpload", "Upload successful: $imageUrl")

                Handler(Looper.getMainLooper()).post {
                    if (imageUrl != null) {
                        callback(true, "Upload successful", imageUrl, publicId)
                    } else {
                        callback(false, "Upload failed: No URL returned", null, null)
                    }
                }

            } catch (e: Exception) {
                Log.e("CloudinaryUpload", "Upload error: ${e.message}", e)
                Handler(Looper.getMainLooper()).post {
                    callback(false, "Upload failed: ${e.message}", null, null)
                }
            }
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(imageUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun deleteImage(
        publicId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                Log.d("CloudinaryDelete", "Deleting image: $publicId")

                val response = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
                )

                val result = response["result"] as? String

                Handler(Looper.getMainLooper()).post {
                    if (result == "ok") {
                        Log.d("CloudinaryDelete", "Delete successful")
                        callback(true, "Image deleted successfully")
                    } else {
                        Log.w("CloudinaryDelete", "Delete failed: $result")
                        callback(false, "Delete failed: $result")
                    }
                }

            } catch (e: Exception) {
                Log.e("CloudinaryDelete", "Delete error: ${e.message}", e)
                Handler(Looper.getMainLooper()).post {
                    callback(false, "Delete failed: ${e.message}")
                }
            }
        }
    }
}