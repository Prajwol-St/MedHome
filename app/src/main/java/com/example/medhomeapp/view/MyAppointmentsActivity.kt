package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.repository.AppointmentManagementRepoImpl
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.utils.LanguageManager
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.PatientAppointmentsViewModel
import com.example.medhomeapp.viewmodel.PatientAppointmentsViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MyAppointmentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val language = LanguageManager.getLanguage(this)

        setContent {
            key(language) {
                MyAppointmentsScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentLanguage = LanguageManager.getLanguage(this)
        val savedLanguage = intent.getStringExtra("current_language")

        if (savedLanguage != null && savedLanguage != currentLanguage) {
            recreate()
        } else if (savedLanguage == null) {
            intent.putExtra("current_language", currentLanguage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen() {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val repo = AppointmentManagementRepoImpl(context)
    val viewModel: PatientAppointmentsViewModel = viewModel(
        factory = PatientAppointmentsViewModelFactory(repo, patientId)
    )

    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val pastAppointments by viewModel.pastAppointments.collectAsState()
    val cancelledAppointments by viewModel.cancelledAppointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<AppointmentModel?>(null) }
    var cancellationReason by remember { mutableStateOf("") }

    // Handle operation result
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
                    Text(stringResource(R.string.title_my_appointments))
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MintGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(stringResource(R.string.tab_upcoming, upcomingAppointments.size))
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(stringResource(R.string.tab_past, pastAppointments.size))
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(stringResource(R.string.tab_cancelled, cancelledAppointments.size))
                    }
                )
            }

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            } else {
                when (selectedTab) {
                    0 -> PatientAppointmentList(
                        appointments = upcomingAppointments,
                        emptyMessage = stringResource(R.string.empty_upcoming),
                        showCancelButton = true,
                        onCancel = { appointment ->
                            selectedAppointment = appointment
                            showCancelDialog = true
                        }
                    )

                    1 -> PatientAppointmentList(
                        appointments = pastAppointments,
                        emptyMessage = stringResource(R.string.empty_past),
                        showCancelButton = false,
                        onCancel = {}
                    )

                    2 -> PatientAppointmentList(
                        appointments = cancelledAppointments,
                        emptyMessage = stringResource(R.string.empty_cancelled),
                        showCancelButton = false,
                        onCancel = {}
                    )
                }
            }
        }
    }

    // Cancel Dialog
    if (showCancelDialog && selectedAppointment != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(stringResource(R.string.dialog_cancel_title))
            },
            text = {
                Column {
                    Text(stringResource(R.string.dialog_cancel_message))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cancellationReason,
                        onValueChange = { if (it.length <= 200) cancellationReason = it },
                        label = { Text(stringResource(R.string.label_reason)) },
                        maxLines = 3,
                        supportingText = {
                            Text(stringResource(R.string.char_count, cancellationReason.length))
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(selectedAppointment!!.appointmentId, cancellationReason)
                        showCancelDialog = false
                        cancellationReason = ""
                    },
                    enabled = cancellationReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.btn_cancel_appointment))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.btn_back))
                }
            }
        )
    }
}

@Composable
fun PatientAppointmentList(
    appointments: List<AppointmentModel>,
    emptyMessage: String,
    showCancelButton: Boolean,
    onCancel: (AppointmentModel) -> Unit
) {
    if (appointments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.EventNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text(
                    text = emptyMessage,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(appointments) { appointment ->
                PatientAppointmentCard(
                    appointment = appointment,
                    showCancelButton = showCancelButton,
                    onCancel = { onCancel(appointment) }
                )
            }
        }
    }
}

@Composable
fun PatientAppointmentCard(
    appointment: AppointmentModel,
    showCancelButton: Boolean,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctorName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = appointment.specialization,
                        fontSize = 13.sp,
                        color = MintGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (appointment.status) {
                        AppConstants.STATUS_PENDING -> Color(0xFFFFE0B2)
                        AppConstants.STATUS_CONFIRMED -> Color(0xFFBBDEFB)
                        AppConstants.STATUS_COMPLETED -> Color(0xFFC8E6C9)
                        AppConstants.STATUS_CANCELLED -> Color(0xFFFFCDD2)
                        else -> Color.LightGray
                    }
                ) {
                    Text(
                        text = appointment.status.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (appointment.status) {
                            AppConstants.STATUS_PENDING -> Color(0xFFE65100)
                            AppConstants.STATUS_CONFIRMED -> Color(0xFF1976D2)
                            AppConstants.STATUS_COMPLETED -> Color(0xFF388E3C)
                            AppConstants.STATUS_CANCELLED -> Color(0xFFC62828)
                            else -> Color.Gray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date & Time
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = DateTimeUtils.formatDateForDisplay(appointment.date),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = appointment.time,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Appointment Type
            if (appointment.appointmentType.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = appointment.appointmentType,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Reason for Visit
            if (appointment.patientNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MintGreen
                    )
                    Text(
                        text = appointment.patientNotes,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Cancellation Details
            if (appointment.status == AppConstants.STATUS_CANCELLED &&
                appointment.cancellationReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFE65100)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.label_cancellation_reason),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = appointment.cancellationReason,
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            // Cancel Button
            if (showCancelButton && appointment.status == AppConstants.STATUS_PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_cancel_appointment))
                }
            }
        }
    }
}