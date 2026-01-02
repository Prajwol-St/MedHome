package com.example.medhomeapp.view

import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.repository.UserRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.view.ui.theme.MintGreen
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

    // Fixed: Removed duplicate sharedPrefs declaration
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
                    // Fixed: Removed duplicate icon/label declarations
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
                        HomeScreenContent(currentUser?.name ?: "User")
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
                        HomeScreenContent(currentUser?.name ?: "User")
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
                        HomeScreenContent(currentUser?.name ?: "User")
                    }
                }
            }
        }
    }
}

// Patient Home Screen (Original)
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = MintGreen),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile),
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.welcome),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val intent = Intent(context, QrActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Text(
            text = "Services",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.height(700.dp)
        ) {
            item {
                FeatureCard(
                    title = stringResource(R.string.health_records),
                    icon = Icons.Default.Description,
                    color = MintGreen,
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
                    color = MintGreen,
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
                    color =MintGreen,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.past_bookings),
                    icon = Icons.Default.Event,
                    color = MintGreen,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.appointments),
                    icon = Icons.Default.CalendarMonth,
                    color = MintGreen,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.calories_calculator),
                    icon = Icons.Default.FitnessCenter,
                    color = MintGreen,
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.blood_donation),
                    icon = Icons.Default.Favorite,
                    color = MintGreen,
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
                    color = MintGreen,
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(26.dp),
                    tint = color
                )
            }

            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                lineHeight = 16.sp
            )
        }
    }
}

