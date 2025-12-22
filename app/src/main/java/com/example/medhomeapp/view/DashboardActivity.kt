package com.example.medhomeapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.Blue10

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {

    val context = LocalContext.current

    data class NavItem(
        val label: String,
        val icon: Int,
        val title: String
    )

    var selectedItem by remember { mutableStateOf(0) }

    val navList = listOf(
        NavItem("Home", R.drawable.baseline_home_24, "MedHome"),
        NavItem("Reminder", R.drawable.baseline_access_time_filled_24, "My Reminders"),
        NavItem("Scan", R.drawable.baseline_qr_code_scanner_24, "Scan QR"),
        NavItem("Notifications", R.drawable.baseline_notifications_24, "Notifications"),
        NavItem("Settings", R.drawable.baseline_settings_24, "App Settings")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = White
                ),
                title = {
                    Text(
                        navList[selectedItem].title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 27.sp
                        )
                    )
                }
            )
        },

        bottomBar = {
            NavigationBar {
                navList.forEachIndexed { index, item ->

                    val isQrButton = item.label == "Scan"

                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        label = { Text(item.label) },
                        icon = {
                            if (isQrButton) {
                                Box(modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(
                                        painter = painterResource(item.icon),
                                        contentDescription = item.label,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(6.dp),
                                        tint = androidx.compose.ui.graphics.Color.Unspecified
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(item.icon),
                                    contentDescription = item.label
                                )
                            }
                        }
                    )
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> ReminderScreen()
                2 -> ScannerScreen()
                3 -> NotificationScreen()
                4 -> SettingsScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboard() {
    DashboardBody()
}