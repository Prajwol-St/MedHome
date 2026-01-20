package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.viewmodel.DoctorAvailabilityViewModel
import com.example.medhomeapp.viewmodel.DoctorAvailabilityViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class DoctorAvailabilityActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DoctorAvailabilityScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAvailabilityScreen() {
    val context = LocalContext.current
    val activity = context as? BaseActivity

    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val repo = DoctorAvailabilityRepoImpl()
    val viewModel: DoctorAvailabilityViewModel = viewModel(
        factory = DoctorAvailabilityViewModelFactory(repo, doctorId)
    )

    val timeSlots by viewModel.allSlots.collectAsState()

    var showAddSlotDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(DateTimeUtils.getCurrentDate()) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("17:00") }
    var selectedDuration by remember { mutableStateOf(30) }

    // Group slots by date
    val groupedSlots = timeSlots.groupBy { it.date }.toSortedMap()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                title = { Text("Set Availability") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSlotDialog = true },
                containerColor = SageGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Time Slots")
            }
        }
    ) { paddingValues ->
        if (groupedSlots.isEmpty()) {
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
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "No time slots added",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Tap the button below to add availability",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedSlots.forEach { (date, slots) ->
                    item {
                        Text(
                            text = DateTimeUtils.formatDateForDisplay(date),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(slots) { slot ->
                        TimeSlotItem(
                            slot = slot,
                            onDelete = {
                                // FIXED: Match current ViewModel signature
                                viewModel.deleteSlot(slot.date, slot.id)
                                Toast.makeText(context, "Slot deleted", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Slot Dialog
    if (showAddSlotDialog) {
        AddTimeSlotDialog(
            selectedDate = selectedDate,
            startTime = startTime,
            endTime = endTime,
            selectedDuration = selectedDuration,
            onDateChange = { selectedDate = it },
            onStartTimeChange = { startTime = it },
            onEndTimeChange = { endTime = it },
            onDurationChange = { selectedDuration = it },
            onConfirm = {
                // Generate time slots based on start, end, and duration
                val slots = generateTimeSlots(
                    doctorId = doctorId,
                    date = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    duration = selectedDuration
                )

                if (slots.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Invalid time range. Please check your inputs.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@AddTimeSlotDialog
                }

                // FIXED: Match current ViewModel signature
                slots.forEach { slot ->
                    viewModel.addSlot(
                        date = slot.date,
                        startTime = slot.startTime,
                        endTime = slot.endTime
                    )
                }

                Toast.makeText(
                    context,
                    "${slots.size} time slots added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                showAddSlotDialog = false
            },
            onDismiss = { showAddSlotDialog = false }
        )
    }
}

@Composable
fun TimeSlotItem(
    slot: TimeSlot,
    onDelete: () -> Unit
) {
    // Calculate duration if not available
    val duration = try {
        slot.duration ?: calculateDuration(slot.startTime, slot.endTime)
    } catch (e: Exception) {
        30 // default
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isBooked) Color.LightGray else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = if (slot.isBooked) Color.Gray else SageGreen,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "${slot.startTime} - ${slot.endTime}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (slot.isBooked) Color.Gray else Color(0xFF2C3E50)
                    )
                    Text(
                        text = "$duration minutes • ${if (slot.isBooked) "Booked" else "Available"}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            if (!slot.isBooked) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

// Helper function to calculate duration
fun calculateDuration(startTime: String, endTime: String): Int {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)

        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            (diffInMillis / (1000 * 60)).toInt()
        } else {
            30 // default
        }
    } catch (e: Exception) {
        30 // default
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimeSlotDialog(
    selectedDate: String,
    startTime: String,
    endTime: String,
    selectedDuration: Int,
    onDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dates = remember {
        (0..30).map { daysAhead ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, daysAhead)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(calendar.time) to DateTimeUtils.formatDateForDisplay(dateFormat.format(calendar.time))
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Time Slots",
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
                // Date Selection
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(DateTimeUtils.formatDateForDisplay(selectedDate))
                }

                // Start Time
                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start: $startTime")
                }

                // End Time
                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End: $endTime")
                }

                // Duration Selection
                Text(
                    text = "Slot Duration",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppConstants.SLOT_DURATIONS.forEach { duration ->
                        FilterChip(
                            selected = selectedDuration == duration,
                            onClick = { onDurationChange(duration) },
                            label = { Text("$duration min") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
            ) {
                Text("Add Slots")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )

    // Date Picker Dialog
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date") },
            text = {
                LazyColumn {
                    items(dates) { (date, displayDate) ->
                        TextButton(
                            onClick = {
                                onDateChange(date)
                                showDatePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(displayDate)
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        TimePickerDialog(
            title = "Select Start Time",
            onTimeSelected = {
                onStartTimeChange(it)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        TimePickerDialog(
            title = "Select End Time",
            onTimeSelected = {
                onEndTimeChange(it)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@Composable
fun TimePickerDialog(
    title: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val times = remember {
        (6..21).flatMap { hour ->
            listOf(0, 30).map { minute ->
                String.format("%02d:%02d", hour, minute)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(times) { time ->
                    TextButton(
                        onClick = { onTimeSelected(time) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(time)
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

// Helper function to generate time slots
fun generateTimeSlots(
    doctorId: String,
    date: String,
    startTime: String,
    endTime: String,
    duration: Int
): List<TimeSlot> {
    val slots = mutableListOf<TimeSlot>()
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    try {
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)

        if (start != null && end != null) {
            val calendar = Calendar.getInstance()
            calendar.time = start

            while (calendar.time.before(end)) {
                val slotStart = sdf.format(calendar.time)
                calendar.add(Calendar.MINUTE, duration)

                if (calendar.time.after(end)) break

                val slotEnd = sdf.format(calendar.time)
                val dayOfWeek = DateTimeUtils.getDayOfWeek(date)

                slots.add(
                    TimeSlot(
                        id = UUID.randomUUID().toString(),
                        doctorId = doctorId,
                        date = date,
                        day = dayOfWeek,
                        startTime = slotStart,
                        endTime = slotEnd,
                        duration = duration,
                        isAvailable = true,
                        isBooked = false,
                        appointmentId = ""
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return slots
}