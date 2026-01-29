package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.NotificationRepositoryImpl
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

@Composable
fun NotificationSettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Get userId from SharedPreferences
    val sharedPrefs =
        (context as BaseActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    // Initialize ViewModel
    val viewModel = remember { NotificationSettingsViewModel(NotificationRepositoryImpl()) }

    // Load preferences when screen opens
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadPreferences(it) }
    }

    // Observe preferences from ViewModel
    val preferences by viewModel.preferences
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF648DDB)
                )
            }
            Text(
                text = stringResource(R.string.notification_settings_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF648DDB)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF648DDB))
            }
        } else {
            Text(
                text = stringResource(R.string.manage_notifications),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF648DDB),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Show error message if any
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            preferences?.let { prefs ->
                NotificationToggleItem(
                    title = stringResource(R.string.appointment_reminders),
                    description = stringResource(R.string.appointment_reminders_desc),
                    checked = prefs.appointmentRemindersEnabled,
                    onCheckedChange = { enabled ->
                        userId?.let { viewModel.toggleAppointmentReminders(it, enabled) }
                    }
                )

                NotificationToggleItem(
                    title = stringResource(R.string.medicine_reminders),
                    description = stringResource(R.string.medicine_reminders_desc),
                    checked = prefs.medicineRemindersEnabled,
                    onCheckedChange = { enabled ->
                        userId?.let { viewModel.toggleMedicineReminders(it, enabled) }
                    }
                )

                NotificationToggleItem(
                    title = stringResource(R.string.booking_confirmations),
                    description = stringResource(R.string.booking_confirmations_desc),
                    checked = prefs.bookingConfirmationsEnabled,
                    onCheckedChange = { enabled ->
                        userId?.let { viewModel.toggleBookingConfirmations(it, enabled) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.notification_sound_vibration),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF648DDB),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                NotificationToggleItem(
                    title = stringResource(R.string.notification_sound),
                    description = stringResource(R.string.notification_sound_desc),
                    checked = prefs.reminderSound,
                    onCheckedChange = { enabled ->
                        userId?.let { viewModel.toggleReminderSound(it, enabled) }
                    }
                )

                NotificationToggleItem(
                    title = stringResource(R.string.notification_vibration),
                    description = stringResource(R.string.notification_vibration_desc),
                    checked = prefs.vibration,
                    onCheckedChange = { enabled ->
                        userId?.let { viewModel.toggleVibration(it, enabled) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF648DDB),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 24.dp),
        thickness = 1.dp,
        color = Color(0xFFEEEEEE)
    )
}
