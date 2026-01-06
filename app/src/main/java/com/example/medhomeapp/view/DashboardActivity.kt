package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.UserViewModel
import kotlinx.coroutines.delay

class DashboardActivity : BaseActivity() {
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
    val viewModel = remember { UserViewModel(UserRepoImpl()) }

    val sharedPrefs =
        (context as BaseActivity).getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)

    val userId = sharedPrefs.getString("user_id", null)
    val currentUser by viewModel.currentUser

    var userType by remember {
        mutableStateOf(
            sharedPrefs.getString("user_type", "patient")!!
                .lowercase().trim()
        )
    }

    LaunchedEffect(userId) {
        userId?.let { viewModel.getUserByID(it) }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            userType = it.role.lowercase().trim()
            sharedPrefs.edit { putString("user_type", userType) }
        }
    }

    var selectedTab by remember { mutableStateOf(0) }

    val isDoctor = userType == "doctor"


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

                // HOME
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.baseline_home_24),
                            stringResource(R.string.home)
                        )
                    },
                    label = { Text(stringResource(R.string.home), fontSize = 11.sp) }
                )

                // REMINDER
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.baseline_access_time_filled_24),
                            stringResource(R.string.reminder)
                        )
                    },
                    label = { Text(stringResource(R.string.reminder), fontSize = 11.sp) }
                )

                // QR (NOW FOR BOTH)
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
                    label = { Text(stringResource(R.string.scan), fontSize = 11.sp) }
                )

                // SETTINGS
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.baseline_settings_24),
                            stringResource(R.string.settings)
                        )
                    },
                    label = { Text(stringResource(R.string.settings), fontSize = 11.sp) }
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
                        currentUser?.let {
                            DoctorHomeScreen(
                                user = it,
                                onSetAvailabilityClick = {
                                    val intent =
                                        DoctorAvailabilityActivity.newIntent(context, it)
                                    context.startActivity(intent)
                                }
                            )
                        }
                    } else {
                        HomeScreen()
                    }
                }

                1 -> {
                    if (!isDoctor) {
                        ReminderScreen()
                    }
                }

                2 -> {
                    LaunchedEffect(Unit) {
                        context.startActivity(
                            Intent(context, QrScannerActivity::class.java)
                        )
                        selectedTab = 0
                    }

                    if (isDoctor) {
                        currentUser?.let {
                            DoctorHomeScreen(
                                user = it,
                                onSetAvailabilityClick = {}
                            )
                        }
                    } else {
                        HomeScreenContent(currentUser?.name ?: "User")
                    }
                }

                3 -> {
                    LaunchedEffect(Unit) {
                        context.startActivity(
                            Intent(context, SettingsActivity::class.java)
                        )
                        selectedTab = 0
                    }

                    if (isDoctor) {
                        currentUser?.let {
                            DoctorHomeScreen(
                                user = it,
                                onSetAvailabilityClick = {}
                            )
                        }
                    } else {
                        HomeScreenContent(currentUser?.name ?: "User")
                    }
                }
            }
        }
    }
}

/* ---------------- PATIENT HOME ---------------- */

@Composable
fun HomeScreenContent(userName: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .verticalScroll(scrollState)
    ) {

        Text(
            "Welcome $userName",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(700.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                FeatureCard(
                    title = "Blood Donation",
                    icon = Icons.Default.Favorite,
                    color = MintGreen
                ) {
                    context.startActivity(
                        Intent(context, BloodDonationActivity::class.java)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, title, tint = color)
            Text(title, fontWeight = FontWeight.Bold)
        }
    }
}
