package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.NotificationRepositoryImpl
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.NotificationSettingsViewModel

class NotificationSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotificationSettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen() {
    val context = LocalContext.current
    val activity = context as BaseActivity
    val scrollState = rememberScrollState()

    val backgroundTint = Color(0xFFF1FBF9)
    val textMain = Color(0xFF2C3E50)

    val sharedPrefs = activity.getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val viewModel = remember { NotificationSettingsViewModel(NotificationRepositoryImpl()) }

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadPreferences(it) }
    }

    val preferences by viewModel.preferences
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text("Notifications", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
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
                .background(backgroundTint)
                .verticalScroll(scrollState)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MintGreen
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Alert Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textMain,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = it,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    preferences?.let { prefs ->
                        NotificationToggleItem(
                            title = "Appointment Reminders",
                            description = "24h and 1h alerts before visits",
                            icon = Icons.Default.CalendarMonth,
                            checked = prefs.appointmentRemindersEnabled
                        ) { viewModel.toggleAppointmentReminders(userId!!, it) }

                        NotificationToggleItem(
                            title = "Medicine Reminders",
                            description = "Daily schedule alerts",
                            icon = Icons.Default.Medication,
                            checked = prefs.medicineRemindersEnabled
                        ) { viewModel.toggleMedicineReminders(userId!!, it) }

                        NotificationToggleItem(
                            title = "Booking Updates",
                            description = "Confirmation and status alerts",
                            icon = Icons.Default.NotificationsActive,
                            checked = prefs.bookingConfirmationsEnabled,
                            isLast = true
                        ) { viewModel.toggleBookingConfirmations(userId!!, it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sound & Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textMain,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    preferences?.let { prefs ->
                        NotificationToggleItem(
                            title = "Alert Sound",
                            description = "Play tones for notifications",
                            icon = Icons.Default.VolumeUp,
                            checked = prefs.reminderSound
                        ) { viewModel.toggleReminderSound(userId!!, it) }

                        NotificationToggleItem(
                            title = "Vibration",
                            description = "Haptic feedback for alerts",
                            icon = Icons.Default.Vibration,
                            checked = prefs.vibration,
                            isLast = true
                        ) { viewModel.toggleVibration(userId!!, it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    isLast: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MintGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MintGreen)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(description, fontSize = 12.sp, color = Color.Gray)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = MintGreen)
            )
        }

        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                thickness = 0.5.dp
            )
        }
    }
}
