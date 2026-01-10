package com.example.medhomeapp.view

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.PackageBookingModel
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.viewmodel.HealthPackageViewModel

class PackageBookingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageId = intent.getStringExtra("package_id")

        setContent {
            PackageBookingsScreen(packageId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageBookingsScreen(packageId: String?) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel = remember {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val doctorId = sharedPrefs.getString("user_id", "") ?: ""

    val bookings by viewModel.doctorBookings
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        if (packageId != null) {
            viewModel.getBookingsByPackage(packageId)
        } else {
            viewModel.getBookingsByDoctor(doctorId)
        }
    }

    val totalBookings = bookings.size
    val activeBookings = bookings.count { it.status == "active" }
    val expiredBookings = bookings.count { it.status == "expired" }
    val cancelledBookings = bookings.count { it.status == "cancelled" }
    val totalRevenue = bookings.filter { it.status == "active" }.sumOf { it.packagePrice }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (packageId != null) "Package Bookings" else "All Bookings",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? PackageBookingsActivity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundCream)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SageGreen
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Analytics Cards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnalyticsCard(
                                title = "Total",
                                value = totalBookings.toString(),
                                icon = Icons.Default.ShoppingCart,
                                color = SageGreen,
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsCard(
                                title = "Active",
                                value = activeBookings.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnalyticsCard(
                                title = "Expired",
                                value = expiredBookings.toString(),
                                icon = Icons.Default.EventBusy,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsCard(
                                title = "Cancelled",
                                value = cancelledBookings.toString(),
                                icon = Icons.Default.Cancel,
                                color = Color(0xFFF44336),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Revenue Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column {
                                        Text(
                                            "Total Revenue",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            "NPR $totalRevenue",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SageGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bookings List
                    if (bookings.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No bookings yet",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                "Bookings will appear here",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    "Recent Bookings",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(bookings) { booking ->
                                BookingCard(booking)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(title, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BookingCard(booking: PackageBookingModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(booking.packageName, fontSize = 14.sp, color = Color.Gray)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        "active" -> SageGreen.copy(alpha = 0.1f)
                        "expired" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        "cancelled" -> Color(0xFFF44336).copy(alpha = 0.1f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        booking.status.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            "active" -> SageGreen
                            "expired" -> Color(0xFFFF9800)
                            "cancelled" -> Color(0xFFF44336)
                            else -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Contact", fontSize = 12.sp, color = Color.Gray)
                    Text(booking.patientContact, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Price", fontSize = 12.sp, color = Color.Gray)
                    Text("NPR ${booking.packagePrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Booked On", fontSize = 12.sp, color = Color.Gray)
                    Text(booking.bookedAt, fontSize = 12.sp, color = TextDark)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Expires On", fontSize = 12.sp, color = Color.Gray)
                    Text(booking.expiresAt, fontSize = 12.sp, color = TextDark)
                }
            }
        }
    }
}