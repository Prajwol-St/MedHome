package com.example.medhomeapp.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.PackageBookingModel
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.LightSage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.viewmodel.HealthPackageViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyPackagesActivity : BaseActivity() {

    private val viewModel by lazy {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPackagesScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
        val patientId = sharedPrefs.getString("user_id", "") ?: ""
        viewModel.getBookingsByPatient(patientId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPackagesScreen(viewModel: HealthPackageViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val patientId = sharedPrefs.getString("user_id", "") ?: ""

    val bookings by viewModel.patientBookings
    val isLoading by viewModel.isLoading

    var showCancelDialog by remember { mutableStateOf(false) }
    var bookingToCancel by remember { mutableStateOf<PackageBookingModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getBookingsByPatient(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Packages", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? MyPackagesActivity)?.finish()
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
            } else if (bookings.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No packages booked yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "Browse packages to get started",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings, key = { it.id }) { booking ->
                        MyPackageCard(
                            booking = booking,
                            onCancel = {
                                bookingToCancel = booking
                                showCancelDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Cancel Confirmation Dialog
        if (showCancelDialog && bookingToCancel != null) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancel Booking?") },
                text = {
                    Text("Are you sure you want to cancel '${bookingToCancel?.packageName}'? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            bookingToCancel?.let { booking ->
                                viewModel.cancelBooking(booking.id) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show()
                                        viewModel.getBookingsByPatient(patientId)
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            showCancelDialog = false
                            bookingToCancel = null
                        }
                    ) {
                        Text("Cancel Booking", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCancelDialog = false
                        bookingToCancel = null
                    }) {
                        Text("Keep Booking")
                    }
                }
            )
        }
    }
}

@Composable
fun MyPackageCard(
    booking: PackageBookingModel,
    onCancel: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val daysRemaining = remember(booking.expiresAt) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val expiryDate = dateFormat.parse(booking.expiresAt)
            if (expiryDate != null) {
                val today = Date()
                val diffInMillis = expiryDate.time - today.time
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                days.toInt()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Package Image
            if (booking.packageImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(booking.packageImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Package Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(LightSage),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = SageGreen.copy(alpha = 0.5f)
                    )
                }
            }

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
                        Text(
                            booking.packageName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Dr. ${booking.doctorName}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Status Badge
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

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Booked On", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            booking.bookedAt,
                            fontSize = 14.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (daysRemaining != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Validity", fontSize = 12.sp, color = Color.Gray)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = when {
                                        daysRemaining <= 0 -> Color(0xFFF44336)
                                        daysRemaining <= 7 -> Color(0xFFFF9800)
                                        else -> SageGreen
                                    }
                                )
                                Text(
                                    when {
                                        daysRemaining <= 0 -> "Expired"
                                        daysRemaining == 1 -> "1 day left"
                                        else -> "$daysRemaining days"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        daysRemaining <= 0 -> Color(0xFFF44336)
                                        daysRemaining <= 7 -> Color(0xFFFF9800)
                                        else -> SageGreen
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Price Paid", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "NPR ${booking.packagePrice}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    }

                    // Cancel button only for active bookings
                    if (booking.status == "active") {
                        OutlinedButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}