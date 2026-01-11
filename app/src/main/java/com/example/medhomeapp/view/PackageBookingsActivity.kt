package com.example.medhomeapp.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.*

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

    val bookings by if (packageId != null) {
        viewModel.packageBookings
    } else {
        viewModel.doctorBookings
    }
    val isLoading by viewModel.isLoading

    var selectedFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (packageId != null) {
            viewModel.getBookingsByPackage(packageId)
        } else {
            viewModel.getBookingsByDoctor(doctorId)
        }
    }

    val filteredBookings = remember(bookings, selectedFilter) {
        when (selectedFilter) {
            "active" -> bookings.filter { it.status == "active" }
            "expired" -> bookings.filter { it.status == "expired" }
            "cancelled" -> bookings.filter { it.status == "cancelled" }
            else -> bookings
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
                                isSelected = selectedFilter == null,
                                onClick = { selectedFilter = null },
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsCard(
                                title = "Active",
                                value = activeBookings.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF4CAF50),
                                isSelected = selectedFilter == "active",
                                onClick = { selectedFilter = "active" },
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
                                isSelected = selectedFilter == "expired",
                                onClick = { selectedFilter = "expired" },
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsCard(
                                title = "Cancelled",
                                value = cancelledBookings.toString(),
                                icon = Icons.Default.Cancel,
                                color = Color(0xFFF44336),
                                isSelected = selectedFilter == "cancelled",
                                onClick = { selectedFilter = "cancelled" },
                                modifier = Modifier.weight(1f)
                            )
                        }

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

                    if (selectedFilter != null) {
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = SageGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Showing: ${selectedFilter?.replaceFirstChar { it.uppercase() }} bookings",
                                    fontSize = 14.sp,
                                    color = SageGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear filter",
                                    tint = SageGreen,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { selectedFilter = null }
                                )
                            }
                        }
                    }

                    if (filteredBookings.isEmpty()) {
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
                                if (selectedFilter != null) "No ${selectedFilter} bookings" else "No bookings yet",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                if (selectedFilter != null) "Try a different filter" else "Bookings will appear here",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    if (selectedFilter != null)
                                        "${selectedFilter?.replaceFirstChar { it.uppercase() }} Bookings"
                                    else
                                        "Recent Bookings",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(filteredBookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    viewModel = viewModel,
                                    onRefresh = {
                                        if (packageId != null) {
                                            viewModel.getBookingsByPackage(packageId)
                                        } else {
                                            viewModel.getBookingsByDoctor(doctorId)
                                        }
                                    }
                                )
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
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, color, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    title,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: PackageBookingModel,
    viewModel: HealthPackageViewModel,
    onRefresh: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showActionDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        booking.patientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "•",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        booking.patientContact,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(booking.status)

                    IconButton(
                        onClick = { showActionDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Actions",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                booking.packageName,
                fontSize = 15.sp,
                color = TextDark,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "NPR ${booking.packagePrice.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SageGreen
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    formatDate(booking.bookedAt),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Text(
                    formatDate(booking.expiresAt),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = {
                Text(
                    "Booking Actions",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Patient: ${booking.patientName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Package: ${booking.packageName}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(16.dp))

                    if (booking.status == "active") {
                        Button(
                            onClick = {
                                viewModel.cancelBooking(booking.id) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        onRefresh()
                                    }
                                }
                                showActionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            )
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Cancel Booking")
                        }

                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.deleteBooking(booking.id) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onRefresh()
                                }
                            }
                            showActionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Delete Permanently")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "active" -> SageGreen.copy(alpha = 0.15f) to SageGreen
        "expired" -> Color(0xFFFF9800).copy(alpha = 0.15f) to Color(0xFFFF9800)
        "cancelled" -> Color(0xFFF44336).copy(alpha = 0.15f) to Color(0xFFF44336)
        else -> Color.Gray.copy(alpha = 0.15f) to Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            status.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}