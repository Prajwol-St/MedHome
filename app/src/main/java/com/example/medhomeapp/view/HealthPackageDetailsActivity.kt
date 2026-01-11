package com.example.medhomeapp.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.medhomeapp.model.HealthPackageModel
import com.example.medhomeapp.model.PackageBookingModel
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.*
import com.example.medhomeapp.viewmodel.HealthPackageViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HealthPackageDetailsActivity : BaseActivity() {

    private val viewModel by lazy {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageId = intent.getStringExtra("package_id") ?: ""

        setContent {
            HealthPackageDetailsScreen(viewModel, packageId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackageDetailsScreen(viewModel: HealthPackageViewModel, packageId: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", "") ?: ""
    val userName = sharedPrefs.getString("user_name", "") ?: ""
    val userEmail = sharedPrefs.getString("user_email", "") ?: ""
    val userContact = sharedPrefs.getString("user_contact", "") ?: ""

    var packageModel by remember { mutableStateOf<HealthPackageModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showBookingDialog by remember { mutableStateOf(false) }

    val daysRemaining = remember(packageModel?.duration) {
        try {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dates = packageModel?.duration?.split(" to ")
            if (dates?.size == 2) {
                val endDate = dateFormatter.parse(dates[1])
                if (endDate != null) {
                    val today = Date()
                    val diffInMillis = endDate.time - today.time
                    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                    days.toInt()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    LaunchedEffect(packageId) {
        viewModel.getPackageById(packageId) { pkg ->
            isLoading = false
            packageModel = pkg
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Package Details", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? HealthPackageDetailsActivity)?.finish()
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
            } else if (packageModel == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Package not found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        // Package Image
                        if (packageModel!!.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(packageModel!!.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Package Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .background(LightSage),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = SageGreen.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    item {
                        // Package Info Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    packageModel!!.packageName,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = LightSage
                                ) {
                                    Text(
                                        packageModel!!.category,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SageGreen,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                HorizontalDivider(color = Color.LightGray)

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoColumn(
                                        icon = Icons.Default.Person,
                                        label = "Doctor",
                                        value = packageModel!!.doctorName
                                    )

                                    if (daysRemaining != null) {
                                        InfoColumn(
                                            icon = Icons.Default.Schedule,
                                            label = "Validity",
                                            value = if (daysRemaining > 0) "$daysRemaining days left" else "Expired"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Description Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Description",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    packageModel!!.fullDescription,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    item {
                        // Included Services Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Included Services",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                packageModel!!.includedServices.forEach { service ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SageGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            service,
                                            fontSize = 14.sp,
                                            color = TextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Book Now Button
                if (daysRemaining != null && daysRemaining > 0) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Price", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "NPR ${packageModel!!.price}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
                                )
                            }

                            Button(
                                onClick = { showBookingDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, "Book", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Book Now",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Booking Confirmation Dialog
        if (showBookingDialog && packageModel != null) {
            AlertDialog(
                onDismissRequest = { showBookingDialog = false },
                title = { Text("Confirm Booking") },
                text = {
                    Column {
                        Text("Package: ${packageModel!!.packageName}")
                        Text("Price: NPR ${packageModel!!.price}")
                        if (daysRemaining != null) {
                            Text("Valid for: $daysRemaining days")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Are you sure you want to book this package?")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val currentDate = dateFormat.format(Date())

                            val calendar = Calendar.getInstance()
                            try {
                                val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                val dates = packageModel!!.duration.split(" to ")
                                if (dates.size == 2) {
                                    val endDate = dateFormatter.parse(dates[1])
                                    if (endDate != null) {
                                        calendar.time = endDate
                                    }
                                }
                            } catch (e: Exception) {
                                calendar.add(Calendar.MONTH, 1)
                            }
                            val expiryDate = dateFormat.format(calendar.time)

                            val booking = PackageBookingModel(
                                id = "",
                                packageId = packageModel!!.id,
                                packageName = packageModel!!.packageName,
                                packagePrice = packageModel!!.price,
                                packageDuration = packageModel!!.duration,
                                packageImageUrl = packageModel!!.imageUrl,
                                patientId = userId,
                                patientName = userName,
                                patientEmail = userEmail,
                                patientContact = userContact,
                                doctorId = packageModel!!.doctorId,
                                doctorName = packageModel!!.doctorName,
                                bookedAt = currentDate,
                                expiresAt = expiryDate,
                                status = "active"
                            )

                            viewModel.createBooking(booking) { success, message ->
                                if (success) {
                                    Toast.makeText(context, "Package booked successfully!", Toast.LENGTH_SHORT).show()
                                    (context as? HealthPackageDetailsActivity)?.finish()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            showBookingDialog = false
                        }
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold, color = SageGreen)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBookingDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoColumn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = SageGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
    }
}