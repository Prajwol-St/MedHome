package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorLeaveModel
import com.example.medhomeapp.repository.LeaveManagementRepoImpl
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.viewmodel.LeaveManagementViewModel
import com.example.medhomeapp.viewmodel.LeaveManagementViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import com.example.medhomeapp.utils.LanguageManager


class ManageLeavesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val language = LanguageManager.getLanguage(this)

        setContent {

            key(language) {
                ManageLeavesScreen()
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
fun ManageLeavesScreen() {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val repo = LeaveManagementRepoImpl()
    val viewModel: LeaveManagementViewModel = viewModel(
        factory = LeaveManagementViewModelFactory(repo, doctorId)
    )

    val leaves by viewModel.leaves.collectAsState()
    val activeLeaves by viewModel.activeLeaves.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    var showAddLeaveDialog by remember { mutableStateOf(false) }

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
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                title = { Text(stringResource(R.string.title_manage_leaves)) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddLeaveDialog = true },
                containerColor = SageGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_leave))
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SageGreen)
            }
        } else if (leaves.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.BeachAccess,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.no_leaves),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = stringResource(R.string.no_leaves_hint),
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Leaves Section
                if (activeLeaves.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.active_upcoming_leaves),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(activeLeaves) { leave ->
                        LeaveCard(
                            leave = leave,
                            onDelete = {
                                viewModel.deleteLeave(leave.leaveId)
                            }
                        )
                    }
                }

                // Past Leaves Section
                val pastLeaves = leaves.filter { !activeLeaves.contains(it) }
                if (pastLeaves.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.past_leaves),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(pastLeaves) { leave ->
                        LeaveCard(
                            leave = leave,
                            onDelete = {
                                viewModel.deleteLeave(leave.leaveId)
                            },
                            isPast = true
                        )
                    }
                }
            }
        }
    }

    // Add Leave Dialog
    if (showAddLeaveDialog) {
        AddLeaveDialog(
            onConfirm = { startDate, endDate, reason, leaveType ->
                viewModel.addLeave(startDate, endDate, reason, leaveType)
                showAddLeaveDialog = false
            },
            onDismiss = { showAddLeaveDialog = false }
        )
    }
}

@Composable
fun LeaveCard(
    leave: DoctorLeaveModel,
    onDelete: () -> Unit,
    isPast: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) Color.LightGray.copy(alpha = 0.5f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    when (leave.leaveType) {
                        AppConstants.LEAVE_TYPE_MEDICAL -> Icons.Default.MedicalServices
                        AppConstants.LEAVE_TYPE_CONFERENCE -> Icons.Default.School
                        AppConstants.LEAVE_TYPE_VACATION -> Icons.Default.BeachAccess
                        else -> Icons.Default.EventBusy
                    },
                    contentDescription = null,
                    tint = if (isPast) Color.Gray else SageGreen,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = leave.leaveType.replaceFirstChar { it.uppercase() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPast) Color.Gray else Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${DateTimeUtils.formatDateForDisplay(leave.startDate)} - ${DateTimeUtils.formatDateForDisplay(leave.endDate)}",
                        fontSize = 14.sp,
                        color = if (isPast) Color.Gray else Color.Gray
                    )

                    if (leave.reason.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = leave.reason,
                            fontSize = 13.sp,
                            color = if (isPast) Color.LightGray else Color.Gray
                        )
                    }

                    // Calculate duration
                    val duration = DateTimeUtils.getTimeDifferenceInDays(leave.endDate) -
                            DateTimeUtils.getTimeDifferenceInDays(leave.startDate) + 1
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$duration day${if (duration > 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = if (isPast) Color.LightGray else SageGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!isPast) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLeaveDialog(
    onConfirm: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf(DateTimeUtils.getCurrentDate()) }
    var endDate by remember { mutableStateOf(DateTimeUtils.getCurrentDate()) }
    var reason by remember { mutableStateOf("") }
    var selectedLeaveType by remember { mutableStateOf(AppConstants.LEAVE_TYPE_PERSONAL) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dates = remember {
        (0..90).map { daysAhead ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, daysAhead)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(calendar.time) to DateTimeUtils.formatDateForDisplay(dateFormat.format(calendar.time))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_leave),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Date
                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(
                            R.string.start_date_format,
                            DateTimeUtils.formatDateForDisplay(startDate)
                        )
                    )
                }

                // End Date
                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(
                            R.string.end_date_format,
                            DateTimeUtils.formatDateForDisplay(endDate)
                        )
                    )
                }

                // Leave Type
                Text(
                    text = stringResource(R.string.leave_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LeaveTypeOption(stringResource(R.string.leave_personal), AppConstants.LEAVE_TYPE_PERSONAL, selectedLeaveType) {
                        selectedLeaveType = it
                    }
                    LeaveTypeOption(stringResource(R.string.leave_medical), AppConstants.LEAVE_TYPE_MEDICAL, selectedLeaveType) {
                        selectedLeaveType = it
                    }
                    LeaveTypeOption(stringResource(R.string.leave_conference), AppConstants.LEAVE_TYPE_CONFERENCE, selectedLeaveType) {
                        selectedLeaveType = it
                    }
                    LeaveTypeOption(stringResource(R.string.leave_vacation), AppConstants.LEAVE_TYPE_VACATION, selectedLeaveType) {
                        selectedLeaveType = it
                    }
                }

                // Reason
                OutlinedTextField(
                    value = reason,
                    onValueChange = { if (it.length <= 200) reason = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.reason_optional)) },
                    maxLines = 3,
                    supportingText = {
                        Text(stringResource(R.string.char_count, reason.length))
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(startDate, endDate, reason, selectedLeaveType) },
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                enabled = DateTimeUtils.compareDates(endDate, startDate) >= 0
            ) {
                Text(stringResource(R.string.add_leave))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )

    // Start Date Picker
    if (showStartDatePicker) {
        AlertDialog(
            onDismissRequest = { showStartDatePicker = false },
            title = { Text(stringResource(R.string.select_start_date)) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(dates) { (date, displayDate) ->
                        TextButton(
                            onClick = {
                                startDate = date
                                // Auto-adjust end date if it's before start date
                                if (DateTimeUtils.compareDates(endDate, date) < 0) {
                                    endDate = date
                                }
                                showStartDatePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(displayDate, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }

    // End Date Picker
    if (showEndDatePicker) {
        AlertDialog(
            onDismissRequest = { showEndDatePicker = false },
            title = { Text(stringResource(R.string.select_end_date)) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(dates.filter { (date, _) ->
                        DateTimeUtils.compareDates(date, startDate) >= 0
                    }) { (date, displayDate) ->
                        TextButton(
                            onClick = {
                                endDate = date
                                showEndDatePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(displayDate, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }
}

@Composable
fun LeaveTypeOption(
    label: String,
    value: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedValue == value,
            onClick = { onSelect(value) },
            colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}