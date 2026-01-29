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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthPackageModel
import com.example.medhomeapp.repository.CommonRepoImpl
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.utils.ImageUtils
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.HealthPackageViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreatePackageActivity : BaseActivity() {

    private lateinit var imageUtils: ImageUtils
    private val commonRepo = CommonRepoImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        imageUtils = ImageUtils(this, this)

        setContent {
            var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


            LaunchedEffect(Unit) {
                imageUtils.registerLaunchers(
                    onImageSelected = { uri ->
                        selectedImageUri = uri
                    }
                )
            }

            CreatePackageScreen(
                imageUtils = imageUtils,
                commonRepo = commonRepo,
                selectedImageUri = selectedImageUri
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePackageScreen(
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
                    Toast.makeText(
                        context,
                        context.getString(R.string.msg_image_uploaded),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.err_upload_failed, message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_package), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? CreatePackageActivity)?.finish()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

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
                                    CircularProgressIndicator(color = MintGreen)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(stringResource(R.string.uploading_image), color = Color.Gray, fontSize = 14.sp)
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
                                            Text(stringResource(R.string.tap_change_image), color = Color.White, fontSize = 14.sp)
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
                                        tint = MintGreen
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(stringResource(R.string.tap_upload_image), color = Color.Gray, fontSize = 14.sp)
                                    Text(stringResource(R.string.optional), color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text(stringResource(R.string.label_package_name)) },
                    placeholder = { Text(stringResource(R.string.placeholder_package_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen,
                        cursorColor = MintGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
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
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, null, tint = MintGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            focusedLabelColor = MintGreen,
                            cursorColor = MintGreen,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
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
                    label = { Text(stringResource(R.string.label_short_description)) },
                    placeholder = { Text(stringResource(R.string.placeholder_short_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    supportingText = { Text("${shortDescription.length}/100", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen,
                        cursorColor = MintGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        unfocusedBorderColor = LightSage
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))


                OutlinedTextField(
                    value = fullDescription,
                    onValueChange = { fullDescription = it },
                    label = { Text(stringResource(R.string.label_full_description)) },
                    placeholder = { Text(stringResource(R.string.placeholder_full_description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen,
                        cursorColor = MintGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        unfocusedBorderColor = LightSage
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))


                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) price = it },
                    label = { Text("Price (NPR)") },
                    placeholder = { Text("1000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("NPR ") },
                    leadingIcon = { Icon(Icons.Default.Payment, null, tint = MintGreen) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen,
                        cursorColor = MintGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        unfocusedBorderColor = LightSage
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.validity_period),
                    fontSize = 14.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.select_validity_dates),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartDatePicker = true },
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, LightSage)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Start Date", fontSize = 12.sp, color = Color.Gray)
                                Icon(Icons.Default.CalendarToday, null, tint = MintGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (startDate != null) dateFormatter.format(Date(startDate!!)) else "Select date",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (startDate != null) TextDark else Color.Gray
                            )
                        }
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndDatePicker = true },
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, LightSage)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("End Date", fontSize = 12.sp, color = Color.Gray)
                                Icon(Icons.Default.Event, null, tint = MintGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (endDate != null) dateFormatter.format(Date(endDate!!)) else stringResource(R.string.select_date),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (endDate != null) TextDark else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = includedServices,
                    onValueChange = { includedServices = it },
                    label = { Text(stringResource(R.string.label_included_services)) },
                    placeholder = { Text(stringResource(R.string.placeholder_included_services)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4,
                    supportingText = { Text(stringResource(R.string.helper_separate_commas), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen,
                        cursorColor = MintGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.label_package_status),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark
                            )
                            Text(
                                if (isActive)
                                    stringResource(R.string.status_visible)
                                else
                                    stringResource(R.string.status_hidden),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MintGreen,
                                checkedTrackColor = MintGreen.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        when {
                            packageName.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_package_name),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            shortDescription.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_short_description),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            fullDescription.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_full_description),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            price.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_price),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            startDate == null || endDate == null -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_validity_dates),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            includedServices.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.err_included_services),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                val servicesList = includedServices.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                val durationString = "${dateFormatter.format(Date(startDate!!))} to ${dateFormatter.format(Date(endDate!!))}"

                                val newPackage = HealthPackageModel(
                                    id = "",
                                    doctorId = doctorId,
                                    doctorName = doctorName,
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
                                    createdAt = currentTime,
                                    updatedAt = currentTime
                                )

                                viewModel.createPackage(newPackage) { success, message ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.package_created_success),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        (context as? CreatePackageActivity)?.recreate()
                                        (context as? CreatePackageActivity)?.finish()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    enabled = !isLoading && !isUploadingImage,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_create_package), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }


        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDate = startDateState.selectedDateMillis
                        showStartDatePicker = false
                    }) {
                        Text("OK", color = MintGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = startDateState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = MintGreen,
                        todayContentColor = MintGreen,
                        todayDateBorderColor = MintGreen
                    )
                )
            }
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDate = endDateState.selectedDateMillis
                        showEndDatePicker = false
                    }) {
                        Text("OK", color = MintGreen, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = endDateState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = MintGreen,
                        todayContentColor = MintGreen,
                        todayDateBorderColor = MintGreen
                    )
                )
            }
        }
    }
}