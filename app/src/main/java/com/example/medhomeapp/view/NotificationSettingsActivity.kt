package com.example.medhomeapp.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R

class NotificationSettingsActivity : ComponentActivity() {
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

    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var appointmentReminders by remember { mutableStateOf(true) }
    var medicationReminders by remember { mutableStateOf(true) }
    var healthTips by remember { mutableStateOf(false) }

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
                    contentDescription = "Back",
                    tint = Color(0xFF648DDB)
                )
            }
            Text(
                text = "Notification Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF648DDB)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Manage Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF648DDB),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        NotificationToggleItem(
            title = "Push Notifications",
            description = "Receive push notifications",
            checked = pushNotifications,
            onCheckedChange = { pushNotifications = it }
        )

        NotificationToggleItem(
            title = "Email Notifications",
            description = "Receive email updates",
            checked = emailNotifications,
            onCheckedChange = { emailNotifications = it }
        )

        NotificationToggleItem(
            title = "Appointment Reminders",
            description = "Get notified about upcoming appointments",
            checked = appointmentReminders,
            onCheckedChange = { appointmentReminders = it }
        )

        NotificationToggleItem(
            title = "Medication Reminders",
            description = "Reminders to take your medications",
            checked = medicationReminders,
            onCheckedChange = { medicationReminders = it }
        )

        NotificationToggleItem(
            title = "Health Tips",
            description = "Receive daily health tips and advice",
            checked = healthTips,
            onCheckedChange = { healthTips = it }
        )

        Spacer(modifier = Modifier.height(80.dp))
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