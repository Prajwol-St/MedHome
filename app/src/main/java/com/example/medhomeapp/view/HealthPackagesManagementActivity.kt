package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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

class HealthPackagesManagementActivity : BaseActivity() {

    private val viewModel by lazy {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthPackagesManagementScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
        val doctorId = sharedPrefs.getString("user_id", "") ?: ""
        viewModel.getPackagesByDoctor(doctorId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesManagementScreen(viewModel: HealthPackageViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val doctorId = sharedPrefs.getString("user_id", "") ?: ""

    val packages by viewModel.doctorPackages
    val isLoading by viewModel.isLoading
    var showDeleteDialog by remember { mutableStateOf(false) }
    var packageToDelete by remember { mutableStateOf<HealthPackageModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getPackagesByDoctor(doctorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Health Packages", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? HealthPackagesManagementActivity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, PackageBookingsActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Assessment, "Insights", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, CreatePackageActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = SageGreen
            ) {
                Icon(Icons.Default.Add, "Create Package", tint = Color.White)
            }
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
            } else if (packages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No packages yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "Create your first health package",
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
                    items(packages, key = { it.id }) { pkg ->
                        DoctorPackageCard(
                            packageModel = pkg,
                            onEdit = {
                                val intent = Intent(context, EditPackageActivity::class.java)
                                intent.putExtra("package_id", pkg.id)
                                context.startActivity(intent)
                            },
                            onDelete = {
                                packageToDelete = pkg
                                showDeleteDialog = true
                            },
                            onViewBookings = {
                                val intent = Intent(context, PackageBookingsActivity::class.java)
                                intent.putExtra("package_id", pkg.id)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && packageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Package?") },
                text = { Text("Are you sure you want to delete '${packageToDelete?.packageName}'? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            packageToDelete?.let { pkg ->
                                viewModel.deletePackage(pkg.id) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, "Package deleted", Toast.LENGTH_SHORT).show()
                                        viewModel.getPackagesByDoctor(doctorId)
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            showDeleteDialog = false
                            packageToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        packageToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DoctorPackageCard(
    packageModel: HealthPackageModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewBookings: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Calculate days remaining
    val daysRemaining = remember(packageModel.duration) {
        try {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dates = packageModel.duration.split(" to ")
            if (dates.size == 2) {
                val endDate = dateFormatter.parse(dates[1])
                if (endDate != null) {
                    val today = Date()
                    val diffInMillis = endDate.time - today.time
                    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                    days.toInt()
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewBookings() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (packageModel.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(packageModel.imageUrl)
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
                            packageModel.packageName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            packageModel.category,
                            fontSize = 12.sp,
                            color = SageGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Edit, "Edit", tint = SageGreen, modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    packageModel.shortDescription,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Price",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "NPR ${packageModel.price}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    }

                    // Days Remaining Badge
                    if (daysRemaining != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                daysRemaining <= 0 -> Color(0xFFF44336).copy(alpha = 0.1f) // Red - Expired
                                daysRemaining <= 7 -> Color(0xFFFF9800).copy(alpha = 0.1f) // Orange - Expiring Soon
                                else -> SageGreen.copy(alpha = 0.1f) // Green - Good
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
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
                                        else -> "$daysRemaining days left"
                                    },
                                    fontSize = 12.sp,
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
            }
        }
    }
}