package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepoImpl
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.viewmodel.DoctorAppointmentsViewModel
import com.example.medhomeapp.viewmodel.DoctorAppointmentsViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class DoctorAppointmentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DoctorAppointmentsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen() {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val repo = AppointmentManagementRepoImpl()
    val viewModel: DoctorAppointmentsViewModel = viewModel(
        factory = DoctorAppointmentsViewModelFactory(repo, doctorId)
    )

    val todayAppointments by viewModel.todayAppointments.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val pastAppointments by viewModel.pastAppointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<AppointmentModel?>(null) }

    LaunchedEffect(operationResult) {
        operationResult?.let { (success, message) ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearOperationResult()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "My Appointments",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { activity?.finish() },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFB),
                            Color(0xFFE8F5F2)
                        )
                    )
                )
        ) {
            // Enhanced Tabs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MintGreen,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .padding(horizontal = 24.dp)
                                .background(MintGreen, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                "Today",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 0) MintGreen else Color(0xFFE0E0E0),
                                        CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${todayAppointments.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 0) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                "Upcoming",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 1) MintGreen else Color(0xFFE0E0E0),
                                        CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${upcomingAppointments.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 1) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                "Past",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == 2) MintGreen else Color(0xFFE0E0E0),
                                        CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${pastAppointments.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 2) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen, strokeWidth = 3.dp)
                }
            } else {
                when (selectedTab) {
                    0 -> AppointmentList(
                        appointments = todayAppointments,
                        emptyMessage = "No appointments for today",
                        showActions = true,
                        onAppointmentClick = {
                            selectedAppointment = it
                            showActionDialog = true
                        }
                    )
                    1 -> AppointmentList(
                        appointments = upcomingAppointments,
                        emptyMessage = "No upcoming appointments",
                        showActions = false,
                        onAppointmentClick = {
                            selectedAppointment = it
                            showActionDialog = true
                        }
                    )
                    2 -> AppointmentList(
                        appointments = pastAppointments,
                        emptyMessage = "No past appointments",
                        showActions = false,
                        onAppointmentClick = {
                            selectedAppointment = it
                            showActionDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showActionDialog && selectedAppointment != null) {
        AppointmentActionDialog(
            appointment = selectedAppointment!!,
            onComplete = { notes ->
                viewModel.completeAppointment(selectedAppointment!!.appointmentId, notes)
                showActionDialog = false
            },
            onNoShow = {
                viewModel.markAsNoShow(selectedAppointment!!.appointmentId)
                showActionDialog = false
            },
            onCancel = { reason ->
                viewModel.cancelAppointment(selectedAppointment!!.appointmentId, reason)
                showActionDialog = false
            },
            onDismiss = { showActionDialog = false }
        )
    }
}

@Composable
fun AppointmentList(
    appointments: List<AppointmentModel>,
    emptyMessage: String,
    showActions: Boolean,
    onAppointmentClick: (AppointmentModel) -> Unit
) {
    if (appointments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = MintGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EventNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MintGreen
                    )
                }
                Text(
                    text = emptyMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = "Your appointments will appear here",
                    fontSize = 14.sp,
                    color = Color(0xFF7F8C8D)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(appointments) { appointment ->
                DoctorAppointmentCard(
                    appointment = appointment,
                    showActions = showActions,
                    onClick = { onAppointmentClick(appointment) }
                )
            }
        }
    }
}

@Composable
fun DoctorAppointmentCard(
    appointment: AppointmentModel,
    showActions: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with patient info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MintGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MintGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = appointment.patientName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF7F8C8D)
                            )
                            Text(
                                text = appointment.patientPhone,
                                fontSize = 13.sp,
                                color = Color(0xFF7F8C8D)
                            )
                        }
                    }
                }

                AppointmentStatusChip(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MintGreen.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MintGreen
                        )
                        Column {
                            Text(
                                text = "Date",
                                fontSize = 11.sp,
                                color = Color(0xFF7F8C8D),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = DateTimeUtils.formatDateForDisplay(appointment.date),
                                fontSize = 13.sp,
                                color = Color(0xFF2C3E50),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MintGreen.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MintGreen
                        )
                        Column {
                            Text(
                                text = "Time",
                                fontSize = 11.sp,
                                color = Color(0xFF7F8C8D),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = appointment.time,
                                fontSize = 13.sp,
                                color = Color(0xFF2C3E50),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Patient Notes
            if (appointment.patientNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9E6), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Notes,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFA000)
                            )
                            Text(
                                text = "Reason for Visit",
                                fontSize = 12.sp,
                                color = Color(0xFFFFA000),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = appointment.patientNotes,
                            fontSize = 13.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Actions
            if (showActions && appointment.status in listOf(AppConstants.STATUS_PENDING, AppConstants.STATUS_CONFIRMED)) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Icon(
                        Icons.Default.ManageAccounts,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Manage Appointment",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentStatusChip(status: String) {
    val (backgroundColor, textColor, icon) = when (status) {
        AppConstants.STATUS_PENDING -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100), Icons.Default.Schedule)
        AppConstants.STATUS_CONFIRMED -> Triple(Color(0xFFBBDEFB), Color(0xFF1976D2), Icons.Default.CheckCircle)
        AppConstants.STATUS_COMPLETED -> Triple(Color(0xFFC8E6C9), Color(0xFF388E3C), Icons.Default.TaskAlt)
        AppConstants.STATUS_CANCELLED -> Triple(Color(0xFFFFCDD2), Color(0xFFC62828), Icons.Default.Cancel)
        AppConstants.STATUS_NO_SHOW -> Triple(Color(0xFFE0E0E0), Color(0xFF616161), Icons.Default.PersonOff)
        else -> Triple(Color.LightGray, Color.Gray, Icons.Default.Info)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentActionDialog(
    appointment: AppointmentModel,
    onComplete: (String) -> Unit,
    onNoShow: () -> Unit,
    onCancel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var doctorNotes by remember { mutableStateOf(appointment.doctorNotes) }
    var cancellationReason by remember { mutableStateOf("") }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFEBEE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text("Cancel Appointment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                OutlinedTextField(
                    value = cancellationReason,
                    onValueChange = { if (it.length <= 200) cancellationReason = it },
                    label = { Text("Cancellation Reason") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    supportingText = { Text("${cancellationReason.length}/200", fontSize = 12.sp) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(cancellationReason)
                        showCancelDialog = false
                    },
                    enabled = cancellationReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Cancel Appointment", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Back", color = Color(0xFF7F8C8D), fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MintGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MintGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Appointment Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Patient Info Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppointmentDetailRow("Patient", appointment.patientName)
                            AppointmentDetailRow("Phone", appointment.patientPhone)
                            AppointmentDetailRow("Date", DateTimeUtils.formatDateForDisplay(appointment.date))
                            AppointmentDetailRow("Time", "${appointment.time} (${appointment.duration} min)")
                        }
                    }

                    if (appointment.patientNotes.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "Patient's Reason",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFA000)
                                )
                                Text(
                                    appointment.patientNotes,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }

                    // Doctor Notes
                    OutlinedTextField(
                        value = doctorNotes,
                        onValueChange = { if (it.length <= 500) doctorNotes = it },
                        label = { Text("Doctor Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        supportingText = { Text("${doctorNotes.length}/500", fontSize = 12.sp) }
                    )
                }
            },
            confirmButton = {
                if (appointment.status in listOf(AppConstants.STATUS_PENDING, AppConstants.STATUS_CONFIRMED)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onComplete(doctorNotes) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Mark Complete", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick = onNoShow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7F8C8D))
                        ) {
                            Icon(Icons.Default.PersonOff, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Mark No-Show", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Cancel Appointment", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }
                    }
                } else {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Close", color = Color(0xFF7F8C8D), fontWeight = FontWeight.Medium)
                    }
                }
            },
            dismissButton = {
                if (appointment.status in listOf(AppConstants.STATUS_PENDING, AppConstants.STATUS_CONFIRMED)) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Close", color = Color(0xFF7F8C8D), fontWeight = FontWeight.Medium)
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun AppointmentDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 13.sp,
            color = Color(0xFF7F8C8D),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF2C3E50),
            fontWeight = FontWeight.SemiBold
        )
    }
}