package com.example.medhomeapp.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class ImageUtils(
    private val activity: Activity,
    private val registryOwner: ActivityResultRegistryOwner
) {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>

    private var onImageSelectedCallback: ((Uri?) -> Unit)? = null
    private var capturedImageUri: Uri? = null
    private var showImageSourceDialog: (() -> Unit)? = null

    fun registerLaunchers(
        onImageSelected: (Uri?) -> Unit,
        onShowDialog: (() -> Unit)? = null
    ) {
        onImageSelectedCallback = onImageSelected
        showImageSourceDialog = onShowDialog
        galleryLauncher = registryOwner.activityResultRegistry.register(
            "galleryLauncher", ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val uri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {
                Log.d("ImageUtils", "Image selected from gallery: $uri")
                onImageSelectedCallback?.invoke(uri)
            } else {
                Log.e("ImageUtils", "Gallery selection cancelled or failed")
            }
        }

        cameraLauncher = registryOwner.activityResultRegistry.register(
            "cameraLauncher", ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && capturedImageUri != null) {
                Log.d("ImageUtils", "Image captured from camera: $capturedImageUri")
                onImageSelectedCallback?.invoke(capturedImageUri)
            } else {
                Log.e("ImageUtils", "Camera capture cancelled or failed")
            }
        }

        cameraPermissionLauncher = registryOwner.activityResultRegistry.register(
            "cameraPermissionLauncher", ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("ImageUtils", "Camera permission granted")
                openCamera()
            } else {
                Log.e("ImageUtils", "Camera permission denied")
            }
        }

        // Storage Permission Launcher (for older Android versions)
        storagePermissionLauncher = registryOwner.activityResultRegistry.register(
            "storagePermissionLauncher", ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("ImageUtils", "Storage permission granted")
                openGallery()
            } else {
                Log.e("ImageUtils", "Storage permission denied")
            }
        }
    }

    fun launchImagePicker() {
        showImageSourceDialog?.invoke() ?: openGallery()
    }
    fun openCamera() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(permission)
        } else {
            val photoFile = File(activity.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            capturedImageUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                photoFile
            )
            Log.d("ImageUtils", "Opening camera with URI: $capturedImageUri")
            cameraLauncher.launch(capturedImageUri!!)
        }
    }

    fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ doesn't need READ_EXTERNAL_STORAGE
            launchGalleryIntent()
        } else {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                storagePermissionLauncher.launch(permission)
            } else {
                launchGalleryIntent()
            }
        }
    }

    private fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        Log.d("ImageUtils", "Opening gallery")
        galleryLauncher.launch(intent)
    }
}