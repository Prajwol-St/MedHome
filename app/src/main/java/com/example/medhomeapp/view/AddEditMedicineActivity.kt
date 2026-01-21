package com.example.medhomeapp.view

import android.app.TimePickerDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.NotificationRepositoryImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.utils.NotificationConstants
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.MedicineReminderViewModel
import java.util.*

class AddEditMedicineActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val medicineId = intent.getStringExtra("medicineId")

        setContent {
            AddEditMedicineScreen(medicineId)
        }
    }
}

@Composable
fun AddEditMedicineScreen(medicineId: String?) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val sharedPrefs = (context as BaseActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val viewModel = remember { MedicineReminderViewModel(NotificationRepositoryImpl()) }

    val isEditMode = medicineId != null

    // Form State
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var reminderTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    var frequency by remember { mutableStateOf(NotificationConstants.FREQUENCY_DAILY) }
    var instructions by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Load medicine data if editing
    LaunchedEffect(medicineId) {
        if (isEditMode && userId != null && medicineId != null) {
            viewModel.getMedicineById(userId, medicineId)
        }
    }

    val selectedMedicine by viewModel.selectedMedicine
    LaunchedEffect(selectedMedicine) {
        selectedMedicine?.let { medicine ->
            medicineName = medicine.medicineName
            dosage = medicine.dosage
            reminderTimes = medicine.reminderTimes
            frequency = medicine.frequency
            instructions = medicine.instructions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MintGreen)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as BaseActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = if (isEditMode) "Edit Medicine" else "Add Medicine",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = message, color = Color(0xFFD32F2F), fontSize = 13.sp)
                    }
                }
            }

            // Medicine Name
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("Medicine Name") },
                placeholder = { Text("e.g., Paracetamol") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    focusedLabelColor = MintGreen
                )
            )

            // Dosage
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage") },
                placeholder = { Text("e.g., 500mg or 2 tablets") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    focusedLabelColor = MintGreen
                )
            )

            // Frequency
            Text(
                text = "Frequency",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FrequencyChip(
                    text = "Daily",
                    selected = frequency == NotificationConstants.FREQUENCY_DAILY,
                    onClick = { frequency = NotificationConstants.FREQUENCY_DAILY }
                )
                FrequencyChip(
                    text = "Weekly",
                    selected = frequency == NotificationConstants.FREQUENCY_WEEKLY,
                    onClick = { frequency = NotificationConstants.FREQUENCY_WEEKLY }
                )
                FrequencyChip(
                    text = "As Needed",
                    selected = frequency == NotificationConstants.FREQUENCY_AS_NEEDED,
                    onClick = { frequency = NotificationConstants.FREQUENCY_AS_NEEDED }
                )
            }

            // Reminder Times
            Text(
                text = "Reminder Times",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            // Display added times
            if (reminderTimes.isNotEmpty()) {
                reminderTimes.forEach { time ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MintGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = time,
                                fontSize = 16.sp,
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    reminderTimes = reminderTimes.filter { it != time }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Time Button
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MintGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Reminder Time")
            }

            // Instructions
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (Optional)") },
                placeholder = { Text("e.g., Take after meal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGreen,
                    focusedLabelColor = MintGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    userId?.let { uid ->
                        viewModel.clearMessages()
                        if (isEditMode && selectedMedicine != null) {
                            viewModel.updateMedicineReminder(
                                medicine = selectedMedicine!!,
                                medicineName = medicineName,
                                dosage = dosage,
                                reminderTimes = reminderTimes,
                                frequency = frequency,
                                instructions = instructions,
                                startDate = System.currentTimeMillis(),
                                endDate = null
                            ) { success ->
                                if (success) {
                                    (context as BaseActivity).finish()
                                }
                            }
                        } else {
                            viewModel.addMedicineReminder(
                                userId = uid,
                                medicineName = medicineName,
                                dosage = dosage,
                                reminderTimes = reminderTimes,
                                frequency = frequency,
                                instructions = instructions,
                                startDate = System.currentTimeMillis(),
                                endDate = null
                            ) { success ->
                                if (success) {
                                    (context as BaseActivity).finish()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isEditMode) "Update Medicine" else "Add Medicine",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                if (!reminderTimes.contains(timeString)) {
                    reminderTimes = (reminderTimes + timeString).sorted()
                }
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}

@Composable
fun FrequencyChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MintGreen else Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, TextGray.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else TextDark,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}