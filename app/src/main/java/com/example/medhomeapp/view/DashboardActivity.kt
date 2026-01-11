package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.UserViewModel

class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardScaffold()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScaffold() {
    val context = LocalContext.current
    val viewModel = remember { UserViewModel(UserRepoImpl()) }

    val sharedPrefs = (context as BaseActivity).getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val currentUser by viewModel.currentUser

    // Initialize userType from SharedPreferences
    var userType by remember {
        mutableStateOf(
            sharedPrefs.getString("user_type", "patient")?.lowercase()?.trim()?.takeIf { it.isNotEmpty() } ?: "patient"
        )
    }

    // Load user data immediately when userId is available
    LaunchedEffect(userId) {
        userId?.let { viewModel.getUserByID(it) }
    }

    // Update userType when currentUser changes - prioritize database role
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val roleFromDb = user.role.lowercase().trim()
            if (roleFromDb.isNotEmpty() && roleFromDb != userType) {
                userType = roleFromDb
                sharedPrefs.edit().putString("user_type", roleFromDb).apply()
            }
        }
    }

    var selectedTab by remember { mutableStateOf(0) }

    // Check role from both SharedPreferences and currentUser, case-insensitive
    val roleFromUser = currentUser?.role?.lowercase()?.trim() ?: ""
    val isDoctor = userType == "doctor" || roleFromUser == "doctor"

    // Reset selectedTab if doctor and tab 2 is selected (Scan QR - not available for doctors)
    LaunchedEffect(isDoctor) {
        if (isDoctor && selectedTab == 2) {
            selectedTab = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> stringResource(R.string.medhome)
                            1 -> stringResource(R.string.reminder)
                            2 -> stringResource(R.string.scan)
                            else -> stringResource(R.string.settings)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(painterResource(R.drawable.baseline_home_24), stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MintGreen,
                        selectedTextColor = MintGreen,
                        indicatorColor = LightSage.copy(alpha = 0.3f),
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(painterResource(R.drawable.baseline_access_time_filled_24), stringResource(R.string.reminder)) },
                    label = { Text(stringResource(R.string.reminder), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MintGreen,
                        selectedTextColor = MintGreen,
                        indicatorColor = LightSage.copy(alpha = 0.3f),
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )

                // Only show Scan QR for patients
                if (!isDoctor) {
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                painterResource(R.drawable.baseline_qr_code_scanner_24),
                                stringResource(R.string.scan),
                                modifier = Modifier.size(28.dp),
                                tint = if (selectedTab == 2) MintGreen else TextGray
                            )
                        },
                        label = { Text(stringResource(R.string.scan), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MintGreen,
                            selectedTextColor = MintGreen,
                            indicatorColor = LightSage.copy(alpha = 0.3f),
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        )
                    )
                }

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(painterResource(R.drawable.baseline_settings_24), stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MintGreen,
                        selectedTextColor = MintGreen,
                        indicatorColor = LightSage.copy(alpha = 0.3f),
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    if (isDoctor) {
                        DoctorHomeScreen(currentUser?.name ?: "Doctor")
                    } else {
                        HomeScreen(currentUser?.name ?: "User")
                    }
                }
                1 -> {
                    if (isDoctor) {
                        DoctorScheduleScreen()
                    } else {
                        ReminderScreen()
                    }
                }
                2 -> {
                    if (!isDoctor) {
                        LaunchedEffect(Unit) {
                            val intent = Intent(context, QrScannerActivity::class.java)
                            context.startActivity(intent)
                            selectedTab = 0
                        }
                        HomeScreen(currentUser?.name ?: "User")
                    }
                }
                3 -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                        selectedTab = 0
                    }
                    if (isDoctor) {
                        DoctorHomeScreen(currentUser?.name ?: "Doctor")
                    } else {
                        HomeScreen(currentUser?.name ?: "User")
                    }
                }
            }
        }
    }

}




