package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.CommonRepoImpl
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.utils.ImageUtils
import com.example.medhomeapp.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : BaseActivity() {
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        imageUtils = ImageUtils(this, this)

        setContent {
            EditProfileScreen(imageUtils)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(imageUtils: ImageUtils) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val viewModel = remember { UserViewModel(UserRepoImpl()) }
    val commonRepo = remember { CommonRepoImpl() }
    val scrollState = rememberScrollState()

    val userId = (context as ComponentActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
        .getString("user_id", null)

    val currentUser by viewModel.currentUser
    var isLoading by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }
    var profilePicturePublicId by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Register image picker
    LaunchedEffect(Unit) {
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
            uri?.let {
                isUploadingImage = true
                commonRepo.uploadImage(context, it, "profile_pictures") { success, message, url, publicId ->
                    isUploadingImage = false
                    if (success && url != null) {
                        if (profilePicturePublicId.isNotEmpty()) {
                            commonRepo.deleteImage(profilePicturePublicId) { _, _ -> }
                        }
                        profilePictureUrl = url
                        profilePicturePublicId = publicId ?: ""
                        Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(userId) {
        userId?.let { viewModel.getUserByID(it) }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            name = it.name
            contact = it.contact
            gender = it.gender
            dateOfBirth = it.dateOfBirth
            bloodGroup = it.bloodGroup
            emergencyContact = it.emergencyContact
            address = it.address
            profilePictureUrl = it.profilePicture
            profilePicturePublicId = it.profilePicturePublicId
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SageGreen)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { (context as ComponentActivity).finish() }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCream),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(SageGreen.copy(alpha = 0.15f))
                            .border(3.dp, SageGreen.copy(alpha = 0.3f), CircleShape)
                            .clickable { imageUtils.launchImagePicker() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = SageGreen,
                                strokeWidth = 3.dp
                            )
                        } else if (profilePictureUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp),
                                tint = SageGreen
                            )
                        }
                    }

                    if (!isUploadingImage) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(SageGreen)
                                .border(3.dp, BackgroundCream, CircleShape)
                                .clickable { imageUtils.launchImagePicker() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_camera_alt_24),
                                contentDescription = "Change Picture",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (profilePictureUrl.isNotEmpty() && !isUploadingImage) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text(
                            "Remove Photo",
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Personal Information",
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Full Name",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter your full name", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contact Number",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    placeholder = { Text("10-digit phone number", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Gender",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                var expandedGender by remember { mutableStateOf(false) }
                val genderOptions = listOf("Male", "Female", "Other")

                ExposedDropdownMenuBox(
                    expanded = expandedGender,
                    onExpandedChange = { expandedGender = !expandedGender },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select gender", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = SageGreen,
                            unfocusedIndicatorColor = LightSage,
                            cursorColor = SageGreen,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedGender,
                        onDismissRequest = { expandedGender = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        color = TextDark,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    gender = option
                                    expandedGender = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = TextDark
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Date of Birth",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { },
                    placeholder = { Text("Select date", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                    enabled = !isLoading,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_calendar_month_24),
                                contentDescription = "Select Date",
                                tint = SageGreen
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Blood Group",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                var expandedBloodGroup by remember { mutableStateOf(false) }
                val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

                ExposedDropdownMenuBox(
                    expanded = expandedBloodGroup,
                    onExpandedChange = { expandedBloodGroup = !expandedBloodGroup },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select blood group", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodGroup)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = SageGreen,
                            unfocusedIndicatorColor = LightSage,
                            cursorColor = SageGreen,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedBloodGroup,
                        onDismissRequest = { expandedBloodGroup = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        group,
                                        color = TextDark,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    bloodGroup = group
                                    expandedBloodGroup = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = TextDark
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Emergency Contact",
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Emergency Contact Number",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    placeholder = { Text("10-digit number", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Address",
                    style = TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = { Text("Enter your address", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = SageGreen,
                        unfocusedIndicatorColor = LightSage,
                        cursorColor = SageGreen,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true

                        val updatedUser = currentUser?.copy(
                            name = name,
                            contact = contact,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                            bloodGroup = bloodGroup,
                            emergencyContact = emergencyContact,
                            address = address,
                            profilePicture = profilePictureUrl,
                            profilePicturePublicId = profilePicturePublicId
                        ) ?: return@Button

                        if (userId != null) {
                            viewModel.editProfile(userId, updatedUser) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    (context as ComponentActivity).finish()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = !isLoading && !isUploadingImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextDark,
                        disabledContainerColor = TextDark.copy(alpha = 0.6f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save Changes",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateOfBirth = dateFormat.format(calendar.time)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = SageGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = BackgroundCream
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = SageGreen,
                    todayDateBorderColor = SageGreen,
                    todayContentColor = SageGreen
                )
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Profile Picture", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove your profile picture?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (profilePicturePublicId.isNotEmpty()) {
                            commonRepo.deleteImage(profilePicturePublicId) { success, message ->
                                if (success) {
                                    profilePictureUrl = ""
                                    profilePicturePublicId = ""
                                    Toast.makeText(context, "Photo removed", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}