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
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.utils.ImageUtils
import com.example.medhomeapp.viewmodel.HealthPackageViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditPackageActivity : BaseActivity() {

    private lateinit var imageUtils: ImageUtils
    private val commonRepo = CommonRepoImpl()
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageId = intent.getStringExtra("package_id") ?: ""

        // Initialize ImageUtils
        imageUtils = ImageUtils(this, this)

        // Register launchers BEFORE setContent
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        setContent {
            EditPackageScreen(
                packageId = packageId,
                imageUtils = imageUtils,
                commonRepo = commonRepo,
                selectedImageUri = selectedImageUri
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPackageScreen(
    packageId: String,
    imageUtils: ImageUtils,
    commonRepo: CommonRepoImpl,
    selectedImageUri: Uri?
) {
    val context = LocalContext.current
    val viewModel = remember {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val doctorId = sharedPrefs.getString("user_id", "") ?: ""
    val doctorName = sharedPrefs.getString("user_name", "Doctor") ?: "Doctor"

    var packageName by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General Checkup") }
    var includedServices by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var uploadedImageUrl by remember { mutableStateOf("") }
    var uploadedImagePublicId by remember { mutableStateOf("") }
    var isUploadingImage by remember { mutableStateOf(false) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

    val startDateState = rememberDatePickerState()
    val endDateState = rememberDatePickerState()

    var showCategoryDropdown by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading
    var isLoadingPackage by remember { mutableStateOf(true) }

    var existingPackage by remember { mutableStateOf<HealthPackageModel?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    val hasChanges = remember(packageName, shortDescription, fullDescription, price, category, includedServices, isActive, uploadedImageUrl, startDate, endDate, existingPackage) {
        existingPackage?.let { pkg ->
            packageName != pkg.packageName ||
                    shortDescription != pkg.shortDescription ||
                    fullDescription != pkg.fullDescription ||
                    price != pkg.price.toString() ||
                    category != pkg.category ||
                    includedServices != pkg.includedServices.joinToString(", ") ||
                    isActive != pkg.isActive ||
                    uploadedImageUrl != pkg.imageUrl ||
                    selectedImageUri != null
        } ?: false
    }

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

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Load existing package data
    LaunchedEffect(packageId) {
        viewModel.getPackageById(packageId) { pkg ->
            if (pkg != null) {
                existingPackage = pkg
                packageName = pkg.packageName
                shortDescription = pkg.shortDescription
                fullDescription = pkg.fullDescription
                price = pkg.price.toString()
                category = pkg.category
                includedServices = pkg.includedServices.joinToString(", ")
                isActive = pkg.isActive
                uploadedImageUrl = pkg.imageUrl
                uploadedImagePublicId = pkg.imagePublicId

                try {
                    val dates = pkg.duration.split(" to ")
                    if (dates.size == 2) {
                        startDate = dateFormatter.parse(dates[0])?.time
                        endDate = dateFormatter.parse(dates[1])?.time
                    }
                } catch (e: Exception) {
                    // Ignore parsing errors
                }
            }
            isLoadingPackage = false
        }
    }

    // Upload image when selectedImageUri changes
    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null) {
            isUploadingImage = true
            commonRepo.uploadImage(
                context,
                selectedImageUri,
                "health_packages"
            ) { success, message, imageUrl, publicId ->
                isUploadingImage = false
                if (success && imageUrl != null) {
                    uploadedImageUrl = imageUrl
                    uploadedImagePublicId = publicId ?: ""
                    Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Package", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) {
                            showExitDialog = true
                        } else {
                            (context as? EditPackageActivity)?.finish()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundCream)
        ) {
            if (isLoadingPackage) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SageGreen
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Image Upload Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    enabled = !isUploadingImage,
                                    onClick = { imageUtils.launchImagePicker() }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isUploadingImage -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = SageGreen)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Uploading image...", color = Color.Gray, fontSize = 14.sp)
                                    }
                                }
                                uploadedImageUrl.isNotEmpty() -> {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(uploadedImageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Package Image",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(40.dp),
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Tap to change image", color = Color.White, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = SageGreen
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Tap to upload package image", color = Color.Gray, fontSize = 14.sp)
                                        Text("(Optional)", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // All form fields (same as Create, just pre-filled)
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Package Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            focusedLabelColor = SageGreen,
                            cursorColor = SageGreen,
                            unfocusedBorderColor = LightSage
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = showCategoryDropdown,
                        onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = SageGreen) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SageGreen,
                                focusedLabelColor = SageGreen,
                                unfocusedBorderColor = LightSage
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shortDescription,
                        onValueChange = { shortDescription = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            focusedLabelColor = SageGreen,
                            unfocusedBorderColor = LightSage
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullDescription,
                        onValueChange = { fullDescription = it },
                        label = { Text("Full Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            focusedLabelColor = SageGreen,
                            unfocusedBorderColor = LightSage
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) price = it },
                        label = { Text("Price (NPR)") },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("NPR ") },
                        leadingIcon = { Icon(Icons.Default.Payment, null, tint = SageGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            focusedLabelColor = SageGreen,
                            unfocusedBorderColor = LightSage
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Validity Period", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedCard(
                            modifier = Modifier.weight(1f).clickable { showStartDatePicker = true },
                            border = BorderStroke(1.dp, LightSage)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("Start Date", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    if (startDate != null) dateFormatter.format(Date(startDate!!)) else "Select",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier.weight(1f).clickable { showEndDatePicker = true },
                            border = BorderStroke(1.dp, LightSage)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("End Date", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    if (endDate != null) dateFormatter.format(Date(endDate!!)) else "Select",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = includedServices,
                        onValueChange = { includedServices = it },
                        label = { Text("Included Services") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            focusedLabelColor = SageGreen,
                            unfocusedBorderColor = LightSage
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Package Status", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    if (isActive) "Visible to patients" else "Hidden",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isActive,
                                onCheckedChange = { isActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SageGreen,
                                    checkedTrackColor = SageGreen.copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.LightGray,
                                    uncheckedBorderColor = Color.Gray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            when {
                                packageName.isBlank() || price.isBlank() || startDate == null || endDate == null -> {
                                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    val servicesList = includedServices.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                    val durationString = "${dateFormatter.format(Date(startDate!!))} to ${dateFormatter.format(Date(endDate!!))}"

                                    val updatedPackage = existingPackage!!.copy(
                                        packageName = packageName,
                                        shortDescription = shortDescription,
                                        fullDescription = fullDescription,
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        category = category,
                                        duration = durationString,
                                        includedServices = servicesList,
                                        imageUrl = uploadedImageUrl,
                                        imagePublicId = uploadedImagePublicId,
                                        isActive = isActive,
                                        updatedAt = currentTime
                                    )

                                    viewModel.updatePackage(packageId, updatedPackage) { success, message ->
                                        if (success) {
                                            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                                            (context as? EditPackageActivity)?.finish()
                                        } else {
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                        enabled = !isLoading && !isUploadingImage && existingPackage != null
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Update Package", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Date Pickers
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { startDate = startDateState.selectedDateMillis; showStartDatePicker = false }) {
                        Text("OK", color = SageGreen)
                    }
                },
                dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
            ) {
                DatePicker(state = startDateState)
            }
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { endDate = endDateState.selectedDateMillis; showEndDatePicker = false }) {
                        Text("OK", color = SageGreen)
                    }
                },
                dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") } }
            ) {
                DatePicker(state = endDateState)
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold) },
                text = {
                    Text("You have unsaved changes. Are you sure you want to leave without saving?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            (context as? EditPackageActivity)?.finish()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336))
                    ) {
                        Text("Leave", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}