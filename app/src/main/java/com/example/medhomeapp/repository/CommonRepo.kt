package com.example.medhomeapp.repository

import android.content.Context
import android.net.Uri

interface CommonRepo {


    fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String,
        callback: (Boolean, String, String?, String?) -> Unit
    )


    fun getFileNameFromUri(context: Context, imageUri: Uri): String?


    fun deleteImage(
        publicId: String,
        callback: (Boolean, String) -> Unit
    )
}