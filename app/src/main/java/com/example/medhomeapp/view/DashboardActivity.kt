package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.UserViewModel

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
    val viewModel = remember { UserViewModel(UserRepoImpl()) }

    val sharedPrefs = (context as ComponentActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val currentUser by viewModel.currentUser

    LaunchedEffect(userId) {
        userId?.let { viewModel.getUserByID(it) }
    }

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> "MedHome"
                            1 -> "My Reminders"
                            2 -> "Scan QR"
                            else -> "App Settings"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(painterResource(R.drawable.baseline_home_24), "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(painterResource(R.drawable.baseline_access_time_filled_24), "Reminder") },
                    label = { Text("Reminder") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Box(modifier = Modifier.padding(top = 4.dp)) {
                            Icon(
                                painterResource(R.drawable.baseline_qr_code_scanner_24),
                                "Scan",
                                modifier = Modifier.size(40.dp).padding(6.dp),
                                tint = androidx.compose.ui.graphics.Color.Unspecified
                            )
                        }
                    },
                    label = { Text("Scan") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(painterResource(R.drawable.baseline_settings_24), "Settings") },
                    label = { Text("Settings") }
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
                0 -> HomeScreenContent(currentUser?.name ?: "User")
                1 -> ReminderScreen()
                2 -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, QrScannerActivity::class.java)
                        context.startActivity(intent)
                        selectedTab = 0
                    }
                    HomeScreenContent(currentUser?.name ?: "User")
                }
                3 -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                        selectedTab = 0
                    }
                    HomeScreenContent(currentUser?.name ?: "User")
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(userName: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(scrollState)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue10),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(36.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Welcome",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val intent = Intent(context, QrActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Features Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(800.dp)
        ) {
            item {
                FeatureCard(
                    title = "Health Records",
                    icon = Icons.Default.Description,
                    onClick = {
                        val intent = Intent(context, HealthRecords::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = "Book Consultation",
                    icon = Icons.Default.VideoCall,
                    onClick = {
                        val intent = Intent(context, BookConsultationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = "AI Health Assistant",
                    icon = Icons.Default.Chat,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = "Past Bookings",
                    icon = Icons.Default.Event,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = "Appointments",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = "Calories Calculator",
                    icon = Icons.Default.FitnessCenter,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = "Blood Donation",
                    icon = Icons.Default.Favorite,
                    onClick = {
                        val intent = Intent(context, BloodDonationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = "Health Packages",
                    icon = Icons.Default.LocalShipping,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Blue10
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}