package com.example.medhomeapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.repository.DoctorAvailabilityRepoImpl
import com.example.medhomeapp.utils.AppConstants
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.view.ui.theme.MintGreen
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

    val groupedSlots = timeSlots.groupBy { it.date }.toSortedMap()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "Set Availability",
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSlotDialog = true },
                containerColor = MintGreen,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Time Slots", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    ) { paddingValues ->
        if (groupedSlots.isEmpty()) {
            Box(
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
                    ),
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
                            Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MintGreen
                        )
                    }
                    Text(
                        text = "No time slots added",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = "Tap the button below to add your availability\nand start accepting appointments",
                        fontSize = 14.sp,
                        color = Color(0xFF7F8C8D),
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
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
                    ),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                groupedSlots.forEach { (date, slots) ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MintGreen.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = DateTimeUtils.formatDateForDisplay(date),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C3E50)
                                )
                            }
                        }
                    }

                    items(slots) { slot ->
                        TimeSlotItem(
                            slot = slot,
                            onDelete = {
                                viewModel.deleteSlot(slot.date, slot.id)
                                Toast.makeText(context, "Slot deleted", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

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
    val duration = try {
        slot.duration ?: calculateDuration(slot.startTime, slot.endTime)
    } catch (e: Exception) {
        30
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isBooked) Color(0xFFF0F0F0) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (slot.isBooked) Color(0xFFE0E0E0) else MintGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = if (slot.isBooked) Color.Gray else MintGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${slot.startTime} - ${slot.endTime}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (slot.isBooked) Color.Gray else Color(0xFF2C3E50)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$duration min",
                            fontSize = 13.sp,
                            color = Color(0xFF7F8C8D),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "•",
                            fontSize = 13.sp,
                            color = Color(0xFF7F8C8D)
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (slot.isBooked) Color(0xFFFFB74D).copy(alpha = 0.2f) else MintGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (slot.isBooked) "Booked" else "Available",
                                fontSize = 12.sp,
                                color = if (slot.isBooked) Color(0xFFFF9800) else MintGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (!slot.isBooked) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun calculateDuration(startTime: String, endTime: String): Int {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)

        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            (diffInMillis / (1000 * 60)).toInt()
        } else {
            30
        }
    } catch (e: Exception) {
        30
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
                        Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Add Time Slots",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MintGreen.copy(alpha = 0.05f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MintGreen.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MintGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        DateTimeUtils.formatDateForDisplay(selectedDate),
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C3E50)
                    )
                }

                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MintGreen.copy(alpha = 0.05f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MintGreen.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MintGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Start: $startTime",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C3E50)
                    )
                }

                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MintGreen.copy(alpha = 0.05f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MintGreen.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MintGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "End: $endTime",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C3E50)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Slot Duration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C3E50)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppConstants.SLOT_DURATIONS.forEach { duration ->
                            FilterChip(
                                selected = selectedDuration == duration,
                                onClick = { onDurationChange(duration) },
                                label = {
                                    Text(
                                        "$duration min",
                                        fontWeight = if (selectedDuration == duration) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MintGreen,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedDuration == duration,
                                    borderColor = if (selectedDuration == duration) MintGreen else Color.Transparent,
                                    borderWidth = 2.dp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Add Slots", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancel", color = Color(0xFF7F8C8D), fontWeight = FontWeight.Medium)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Text(
                    "Select Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(dates) { (date, displayDate) ->
                        TextButton(
                            onClick = {
                                onDateChange(date)
                                showDatePicker = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                displayDate,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2C3E50)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

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
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(times) { time ->
                    TextButton(
                        onClick = { onTimeSelected(time) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            time,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF2C3E50)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

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