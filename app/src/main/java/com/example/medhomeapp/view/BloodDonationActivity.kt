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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.R
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.model.DonorModel
import com.example.medhomeapp.repository.BloodDonationRepo
import com.example.medhomeapp.repository.BloodDonationRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.BloodDonationViewModel
import java.text.SimpleDateFormat
import java.util.*

class BloodDonationActivity : ComponentActivity() {
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
    val bloodGroups = listOf("All", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    var selectedGroup by remember { mutableStateOf("All") }

    val bloodRequests by viewModel.bloodRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllBloodRequests()
    }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup == "All") {
            viewModel.getAllBloodRequests()
        } else {
            viewModel.getBloodRequestsByGroup(selectedGroup)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
                title = { Text("Blood Donation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
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
                            contentColor = Color.White,
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
                    containerColor = Blue10,
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_person_add_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                FloatingActionButton(
                    onClick = onPostRequestClick,
                    containerColor = Blue10,
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
                                selectedContainerColor = Blue10,
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
                    text = "Nearby Blood Requests",
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
                        CircularProgressIndicator(color = Blue10)
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
                            text = error ?: "An error occurred",
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
                            text = "No blood requests available",
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
                        text = request.patientName.ifEmpty { "Anonymous" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${request.unitsNeeded} unit needed",
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
                        text = request.urgency,
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
                    color = Blue10,
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
                        containerColor = Blue10
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Contact",
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

@Composable
fun PostBloodRequestScreen(
    viewModel: BloodDonationViewModel,
    bloodRequest: BloodRequestModel?,
    isEditMode: Boolean,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
){

}