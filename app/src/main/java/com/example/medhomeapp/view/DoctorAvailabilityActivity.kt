package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.ui.theme.Blue10
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TimeSlot(
    val day: String,
    val startTime: String,
    val endTime: String,
    val isAvailable: Boolean = true
)

class DoctorAvailabilityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoctorAvailabilityScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAvailabilityScreen() {
    var selectedDay by remember { mutableStateOf("Monday") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("17:00") }
    var timeSlots by remember { mutableStateOf(mutableListOf<TimeSlot>()) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isSelectingStartTime by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as Activity

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "Set Availability",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Select Day",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue10
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day selector
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDay,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue10,
                                focusedLabelColor = Blue10
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            daysOfWeek.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(day) },
                                    onClick = {
                                        selectedDay = day
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Set Time Range",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue10
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Start Time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Start Time",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Blue10, RoundedCornerShape(8.dp))
                                    .clickable {
                                        isSelectingStartTime = true
                                        showTimePicker = true
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    startTime,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // End Time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "End Time",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Blue10, RoundedCornerShape(8.dp))
                                    .clickable {
                                        isSelectingStartTime = false
                                        showTimePicker = true
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    endTime,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            timeSlots.add(
                                TimeSlot(
                                    day = selectedDay,
                                    startTime = startTime,
                                    endTime = endTime
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue10),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Time Slot", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                "Your Schedule",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Blue10
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (timeSlots.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No time slots added yet",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timeSlots) { slot ->
                        TimeSlotCard(
                            slot = slot,
                            onDelete = { timeSlots.remove(slot) }
                        )
                    }
                }
            }
        }
    }


    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                if (isSelectingStartTime) {
                    startTime = time
                } else {
                    endTime = time
                }
                showTimePicker = false
            }
        )
    }
}

@Composable
fun TimeSlotCard(
    slot: TimeSlot,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    slot.day,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue10
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${slot.startTime} - ${slot.endTime}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}