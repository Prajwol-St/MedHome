package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel
import com.example.medhomeapp.repository.BloodDonationRepo
import com.example.medhomeapp.repository.BloodDonationRepoImpl
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.BloodDonationViewModel
import java.text.SimpleDateFormat
import java.util.*

class BloodDonationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = BloodDonationRepoImpl()
            val viewModel: BloodDonationViewModel = viewModel(
                factory = BloodDonationViewModelFactory(repository)
            )
            BloodDonationBody(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationBody(viewModel: BloodDonationViewModel) {
    val context = LocalContext.current
    val activity = context as Activity
    var currentScreen by remember { mutableStateOf("main") }
    var selectedRequest by remember { mutableStateOf<BloodRequestModel?>(null) }
    var isEditMode by remember { mutableStateOf(false) }

    when (currentScreen) {
        "main" -> {
            MainDonationScreen(
                activity = activity,
                viewModel = viewModel,
                onPostRequestClick = {
                    isEditMode = false
                    selectedRequest = null
                    currentScreen = "post_request"
                },
                onJoinDonorClick = { currentScreen = "join_donor" },
                onHistoryClick = { currentScreen = "history" },
                onRequestClick = { request ->
                    selectedRequest = request
                    currentScreen = "details"
                }
            )
        }

        "post_request" -> {
            PostBloodRequestScreen(
                viewModel = viewModel,
                bloodRequest = if (isEditMode) selectedRequest else null,
                isEditMode = isEditMode,
                onBackClick = { currentScreen = "main" },
                onSuccess = { currentScreen = "main" }
            )
        }

        "join_donor" -> {
            JoinDonorListScreen(
                viewModel = viewModel,
                onBackClick = { currentScreen = "main" }
            )
        }

        "history" -> {
            HistoryScreen(
                viewModel = viewModel,
                onBackClick = { currentScreen = "main" },
                onRequestClick = { request ->
                    selectedRequest = request
                    currentScreen = "details"
                }
            )
        }

        "details" -> {
            BloodRequestDetailsScreen(
                request = selectedRequest,
                viewModel = viewModel,
                onBackClick = { currentScreen = "main" },
                onEditClick = {
                    isEditMode = true
                    currentScreen = "post_request"
                },
                onDeleteSuccess = { currentScreen = "main" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDonationScreen(
    activity: Activity,
    viewModel: BloodDonationViewModel,
    onPostRequestClick: () -> Unit,
    onJoinDonorClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onRequestClick: (BloodRequestModel) -> Unit
) {
    val bloodGroups = listOf(
        stringResource(R.string.all_blood_groups),
        "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    )
    var selectedGroup by remember { mutableStateOf(bloodGroups[0]) }

    val bloodRequests by viewModel.bloodRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllBloodRequests()
    }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup == bloodGroups[0]) { // "All"
            viewModel.getAllBloodRequests()
        } else {
            viewModel.getBloodRequestsByGroup(selectedGroup)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
                title = { Text(stringResource(R.string.blood_donation_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }, modifier = Modifier.testTag("backButton")) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onHistoryClick,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = White,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_history_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onJoinDonorClick,
                    containerColor = MintGreen,
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_person_add_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                FloatingActionButton(
                    modifier = Modifier.testTag("createRequestButton"),
                    onClick = onPostRequestClick,
                    containerColor = MintGreen,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bloodGroups.size) { index ->
                        val group = bloodGroups[index]
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = group },
                            label = { Text(group) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MintGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF2D3436)
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.nearby_blood_requests),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MintGreen)
                    }
                }
            }

            if (error != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = error ?: stringResource(R.string.error_occurred),
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            if (bloodRequests.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_blood_requests),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            items(bloodRequests.size) { index ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BloodRequestCard(
                        request = bloodRequests[index],
                        onCardClick = { onRequestClick(bloodRequests[index]) },
                        onContactClick = { request ->
                            // Handle contact click
                        }
                    )
                }

                if (index < bloodRequests.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun BloodRequestCard(
    request: BloodRequestModel,
    onCardClick: () -> Unit,
    onContactClick: (BloodRequestModel) -> Unit
) {
    val timeAgo = remember(request.timestamp) {
        getTimeAgo(request.timestamp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.patientName.ifEmpty { stringResource(R.string.anonymous) },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (request.unitsNeeded == "1") {
                            stringResource(R.string.unit_needed, request.unitsNeeded)
                        } else {
                            stringResource(R.string.units_needed, request.unitsNeeded)
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = when (request.urgency) {
                        "Urgent" -> Color(0xFFFFEBEE)
                        "Within 24 hours" -> Color(0xFFFFF8E1)
                        else -> Color(0xFFE8F5E9)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (request.urgency) {
                            "Urgent" -> stringResource(R.string.urgent)
                            "Within 24 hours" -> stringResource(R.string.within_24_hours)
                            "Within a week" -> stringResource(R.string.within_a_week)
                            else -> request.urgency
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when (request.urgency) {
                            "Urgent" -> Color(0xFFD32F2F)
                            "Within 24 hours" -> Color(0xFFF57C00)
                            else -> Color(0xFF388E3C)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MintGreen,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = request.bloodGroup,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = request.hospital,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                    Text(
                        text = request.location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_filled_24),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { onContactClick(request) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.contact),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        years > 0 -> "$years year${if (years > 1) "s" else ""} ago"
        months > 0 -> "$months month${if (months > 1) "s" else ""} ago"
        weeks > 0 -> "$weeks week${if (weeks > 1) "s" else ""} ago"
        days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
        hours > 0 -> "$hours hr${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "$minutes min${if (minutes > 1) "s" else ""} ago"
        else -> "Just now"
    }
}

fun getFormattedDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


class BloodDonationViewModelFactory(
    private val repository: BloodDonationRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodDonationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BloodDonationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBloodRequestScreen(
    viewModel: BloodDonationViewModel,
    bloodRequest: BloodRequestModel?,
    isEditMode: Boolean,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
){
    var patientName by remember { mutableStateOf(bloodRequest?.patientName ?: "") }
    var bloodGroup by remember { mutableStateOf(bloodRequest?.bloodGroup ?: "") }
    var unitsNeeded by remember { mutableStateOf(bloodRequest?.unitsNeeded ?: "1") }
    var hospital by remember { mutableStateOf(bloodRequest?.hospital ?: "") }
    var location by remember { mutableStateOf(bloodRequest?.location ?: "") }
    var contactNumber by remember { mutableStateOf(bloodRequest?.contactNumber ?: "") }
    var urgencyLevel by remember { mutableStateOf(bloodRequest?.urgency ?: "") }
    var additionalNotes by remember { mutableStateOf(bloodRequest?.additionalNotes ?: "") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onSuccess()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                title = {
                    Text(
                        stringResource(if (isEditMode) R.string.edit_blood_request else R.string.post_blood_request),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ){padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = error ?: "",
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item {
                Text(stringResource(R.string.patient_name), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    placeholder = { Text(stringResource(R.string.patient_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        focusedLabelColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Text(stringResource(R.string.required_blood_group), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded && !isLoading }
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(stringResource(R.string.select_blood_group)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen
                        ),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = {
                                    bloodGroup = group
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(stringResource(R.string.units_needed_label), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = unitsNeeded,
                    onValueChange = { unitsNeeded = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Text(stringResource(R.string.hospital), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    placeholder = { Text(stringResource(R.string.hospital_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Text(stringResource(R.string.location), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text(stringResource(R.string.location_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Text(stringResource(R.string.contact_number), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = { Text(stringResource(R.string.contact_number_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Text(stringResource(R.string.urgency_level), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                var urgencyExpanded by remember { mutableStateOf(false) }
                val urgencyLevels = listOf(
                    "Urgent" to stringResource(R.string.urgent),
                    "Within 24 hours" to stringResource(R.string.within_24_hours),
                    "Within a week" to stringResource(R.string.within_a_week)
                )
                ExposedDropdownMenuBox(
                    expanded = urgencyExpanded,
                    onExpandedChange = { urgencyExpanded = !urgencyExpanded && !isLoading }
                ) {
                    OutlinedTextField(
                        value = urgencyLevels.find { it.first == urgencyLevel }?.second ?: urgencyLevel,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(stringResource(R.string.select_urgency)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(urgencyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen
                        ),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = urgencyExpanded,
                        onDismissRequest = { urgencyExpanded = false }
                    ) {
                        urgencyLevels.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    urgencyLevel = key
                                    urgencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                Text(stringResource(R.string.additional_notes), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    placeholder = { Text(stringResource(R.string.additional_notes_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen
                    ),
                    enabled = !isLoading
                )
            }
            item {
                Button(
                    onClick = {
                        viewModel.postBloodRequest(
                            patientName = patientName,
                            bloodGroup = bloodGroup,
                            unitsNeeded = unitsNeeded,
                            hospital = hospital,
                            location = location,
                            contactNumber = contactNumber,
                            urgencyLevel = urgencyLevel,
                            additionalNotes = additionalNotes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            stringResource(if (isEditMode) R.string.update_blood_request else R.string.post_blood_request),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: BloodDonationViewModel,
    onBackClick: () -> Unit,
    onRequestClick: (BloodRequestModel) -> Unit
){
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.my_requests),
        stringResource(R.string.donor_profile)
    )

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MintGreen,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = { Text(stringResource(R.string.history), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = Color.White
                            )
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MintGreen,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.testTag("myRequestsTab"),
                            text = { Text(title, fontWeight = FontWeight.Medium) }
                        )
                    }
                }
            }
        }
    ){padding ->
        when(selectedTab){
            0 -> MyRequestsTab(
                viewModel = viewModel,
                padding = padding,
                onRequestClick = onRequestClick
            )
            1 -> DonorProfileTab(
                viewModel = viewModel,
                padding = padding
            )
        }
    }
}

@Composable
fun MyRequestsTab(
    viewModel: BloodDonationViewModel,
    padding: PaddingValues,
    onRequestClick: (BloodRequestModel) -> Unit
){
    val bloodRequests by viewModel.bloodRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()

    val userRequests = remember(bloodRequests, currentUserId) {
        bloodRequests.filter { it.userId == currentUserId }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserBloodRequests()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFFFF5F5))
            .padding(16.dp)
    ){
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            }
        }
        if (userRequests.isEmpty() && !isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_blood_requests_yet),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.posted_requests_appear_here),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        items(userRequests.size) { index ->
            BloodRequestCard(
                request = userRequests[index],
                onCardClick = { onRequestClick(userRequests[index]) },
                onContactClick = { }
            )

            if (index < userRequests.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DonorProfileTab(
    viewModel: BloodDonationViewModel,
    padding: PaddingValues
){
    val donorProfile by viewModel.donorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDonorProfile()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFFFF5F5))
            .padding(16.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            }
        }

        if (donorProfile == null && !isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_person_add_24),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.not_registered_as_donor),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.join_donor_list),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        donorProfile?.let { profile ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header with blood group
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.donor_profile),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                            Surface(
                                color = MintGreen,
                                shape = CircleShape,
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = profile.bloodGroup,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.availability_status),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (profile.isAvailable) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                                        shape = CircleShape,
                                        modifier = Modifier.size(12.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(if (profile.isAvailable) R.string.available else R.string.not_available),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (profile.isAvailable) Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.emergency_available),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = stringResource(if (profile.isEmergencyAvailable) R.string.yes else R.string.no),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                        }
                        if (profile.location.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.location),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = profile.location,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3436),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                        if (profile.contactNumber.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.contact_number),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = profile.contactNumber,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3436)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.total_donations),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${profile.totalDonations}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                        }
                        if (profile.lastDonationDate > 0) {
                            Divider()
                            Column {
                                Text(
                                    text = stringResource(R.string.last_donation),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = getTimeAgo(profile.lastDonationDate),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3436)
                                )
                                val daysUntilNext = profile.daysUntilNextDonation()
                                if (daysUntilNext > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFFFF8E1)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFFF57C00),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.can_donate_again_in, daysUntilNext),
                                                fontSize = 13.sp,
                                                color = Color(0xFFF57C00),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                } else if (profile.canDonate()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFE8F5E9)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF388E3C),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.eligible_to_donate),
                                                fontSize = 13.sp,
                                                color = Color(0xFF388E3C),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.registered),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = getTimeAgo(profile.timestamp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MintGreen),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.thank_you_donor),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.generosity_saves_lives),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        if (profile.totalDonations > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color.White.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile.totalDonations}",
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.donations),
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${profile.totalDonations * 3}",
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.lives_saved),
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.donation_guidelines),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )

                        DonationGuideline(
                            icon = Icons.Default.DateRange,
                            text = stringResource(R.string.guideline_wait_90_days)
                        )

                        DonationGuideline(
                            icon = Icons.Default.Favorite,
                            text = stringResource(R.string.guideline_stay_healthy)
                        )

                        DonationGuideline(
                            icon = Icons.Default.Info,
                            text = stringResource(R.string.guideline_save_lives)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DonationGuideline(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = MintGreen.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MintGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF2D3436)
        )
    }
}