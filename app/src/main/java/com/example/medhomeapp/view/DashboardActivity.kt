package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.UserViewModel

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

    val sharedPrefs = (context as BaseActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
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
                            0 -> stringResource(R.string.medhome)
                            1 -> stringResource(R.string.reminder)
                            2 -> stringResource(R.string.scan)
                            else -> stringResource(R.string.settings)
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
                    icon = { Icon(painterResource(R.drawable.baseline_home_24), stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(painterResource(R.drawable.baseline_access_time_filled_24), stringResource(R.string.reminder)) },
                    label = { Text(stringResource(R.string.reminder)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Box(modifier = Modifier.padding(top = 4.dp)) {
                            Icon(
                                painterResource(R.drawable.baseline_qr_code_scanner_24),
                                stringResource(R.string.scan),
                                modifier = Modifier.size(40.dp).padding(6.dp),
                                tint = androidx.compose.ui.graphics.Color.Unspecified
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.scan)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(painterResource(R.drawable.baseline_settings_24), stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) }
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
                            contentDescription = stringResource(R.string.profile),
                            modifier = Modifier.size(36.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.welcome),
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(800.dp)
        ) {
            item {
                FeatureCard(
                    title = stringResource(R.string.health_records),
                    icon = Icons.Default.Description,
                    onClick = {
                        val intent = Intent(context, HealthRecords::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.book_consultation),
                    icon = Icons.Default.VideoCall,
                    onClick = {
                        val intent = Intent(context, BookConsultationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.ai_health_assistant),
                    icon = Icons.Default.Chat,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.past_bookings),
                    icon = Icons.Default.Event,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.appointments),
                    icon = Icons.Default.CalendarMonth,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.calories_calculator),
                    icon = Icons.Default.FitnessCenter,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.blood_donation),
                    icon = Icons.Default.Favorite,
                    onClick = {
                        val intent = Intent(context, BloodDonationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.health_packages),
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