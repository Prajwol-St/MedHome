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
import androidx.compose.ui.res.stringResource
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
    val activity = context as? BaseActivity
    val scrollState = rememberScrollState()

    // Using your app's design tokens
    val backgroundTint = Color(0xFFF1FBF9)
    val textMain = Color(0xFF2C3E50)

    // Get userId from SharedPreferences
    val sharedPrefs = activity?.getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs?.getString("user_id", null)

    // Initialize ViewModel
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
                    Text(
                        stringResource(R.string.notification_settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.back),
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
                    color = MintGreen,
                    trackColor = MintGreen.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Alert Preferences
            Text(
                text = stringResource(R.string.manage_notifications),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textMain,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(error, color = Color(0xFFD32F2F), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    preferences?.let { prefs ->
                        NotificationToggleItem(
                            title = stringResource(R.string.appointment_reminders),
                            description = stringResource(R.string.appointment_reminders_desc),
                            icon = Icons.Default.CalendarMonth,
                            checked = prefs.appointmentRemindersEnabled,
                            onCheckedChange = { userId?.let { id -> viewModel.toggleAppointmentReminders(id, it) } }
                        )
                        NotificationToggleItem(
                            title = stringResource(R.string.medicine_reminders),
                            description = stringResource(R.string.medicine_reminders_desc),
                            icon = Icons.Default.Medication,
                            checked = prefs.medicineRemindersEnabled,
                            onCheckedChange = { userId?.let { id -> viewModel.toggleMedicineReminders(id, it) } }
                        )
                        NotificationToggleItem(
                            title = stringResource(R.string.booking_confirmations),
                            description = stringResource(R.string.booking_confirmations_desc),
                            icon = Icons.Default.NotificationsActive,
                            checked = prefs.bookingConfirmationsEnabled,
                            onCheckedChange = { userId?.let { id -> viewModel.toggleBookingConfirmations(id, it) } },
                            isLast = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Sound & Vibration
            Text(
                text = stringResource(R.string.notification_sound_vibration),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textMain,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    preferences?.let { prefs ->
                        NotificationToggleItem(
                            title = stringResource(R.string.notification_sound),
                            description = stringResource(R.string.notification_sound_desc),
                            icon = Icons.Default.VolumeUp,
                            checked = prefs.reminderSound,
                            onCheckedChange = { userId?.let { id -> viewModel.toggleReminderSound(id, it) } }
                        )
                        NotificationToggleItem(
                            title = stringResource(R.string.notification_vibration),
                            description = stringResource(R.string.notification_vibration_desc),
                            icon = Icons.Default.Vibration,
                            checked = prefs.vibration,
                            onCheckedChange = { userId?.let { id -> viewModel.toggleVibration(id, it) } },
                            isLast = true
                        )
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
    onCheckedChange: (Boolean) -> Unit,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box styled like your FeatureCards
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MintGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MintGreen, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2C3E50))
                Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MintGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                thickness = 0.5.dp,
                color = Color(0xFFEEEEEE)
            )
        }
    }
}
