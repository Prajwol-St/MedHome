package com.example.medhomeapp.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.HealthPackageModel
import com.example.medhomeapp.repository.CommonRepoImpl
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.utils.ImageUtils
import com.example.medhomeapp.viewmodel.HealthPackageViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditPackageActivity : BaseActivity() {

    private lateinit var imageUtils: ImageUtils
    private val commonRepo = CommonRepoImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageUtils = ImageUtils(this, this)
        val packageId = intent.getStringExtra("package_id") ?: ""

        setContent {
            EditPackageScreen(packageId, imageUtils, commonRepo)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPackageScreen(packageId: String, imageUtils: ImageUtils, commonRepo: CommonRepoImpl) {
    val context = LocalContext.current
    val viewModel = remember {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    var packageName by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General Checkup") }
    var duration by remember { mutableStateOf("") }
    var includedServices by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var uploadedImageUrl by remember { mutableStateOf("") }
    var uploadedImagePublicId by remember { mutableStateOf("") }
    var isUploadingImage by remember { mutableStateOf(false) }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading
    var isLoadingPackage by remember { mutableStateOf(true) }

    val categories = listOf(
        "General Checkup",
        "Diabetes Care",
        "Heart Health",
        "Wellness & Fitness",
        "Women's Health",
        "Senior Care",
        "Preventive Care",
        "Chronic Disease Management"
    )

    // Load existing package data
    LaunchedEffect(packageId) {
        viewModel.getPackageById(packageId) { pkg ->
            if (pkg != null) {
                packageName = pkg.packageName
                shortDescription = pkg.shortDescription
                fullDescription = pkg.fullDescription
                price = pkg.price.toString()
                category = pkg.category
                duration = pkg.duration
                includedServices = pkg.includedServices.joinToString(", ")
                isActive = pkg.isActive
                uploadedImageUrl = pkg.imageUrl
                uploadedImagePublicId = pkg.imagePublicId
            }
            isLoadingPackage = false
        }
    }

// Register image picker
    LaunchedEffect(Unit) {
        imageUtils.registerLaunchers { uri ->
            if (uri != null) {
                isUploadingImage = true
                commonRepo.uploadImage(
                    context,
                    uri,
                    "health_packages"
                ) { success, message, imageUrl, publicId ->
                    isUploadingImage = false
                    if (success && imageUrl != null) {
                        uploadedImageUrl = imageUrl
                        uploadedImagePublicId = publicId ?: ""
                        Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Upload failed: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}