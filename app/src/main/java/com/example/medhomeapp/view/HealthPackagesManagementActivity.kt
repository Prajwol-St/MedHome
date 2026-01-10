package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.HealthPackageModel
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.repository.PackageBookingRepoImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.viewmodel.HealthPackageViewModel

class HealthPackagesManagementActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthPackagesManagementScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesManagementScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel = remember {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)
    val doctorId = sharedPrefs.getString("user_id", "") ?: ""

    val packages by viewModel.doctorPackages
    val isLoading by viewModel.isLoading
    var showDeleteDialog by remember { mutableStateOf(false) }
    var packageToDelete by remember { mutableStateOf<HealthPackageModel?>(null) }

    // Load doctor's packages
    LaunchedEffect(Unit) {
        viewModel.getPackagesByDoctor(doctorId)
    }

    // Refresh when returning from other screens
    LaunchedEffect(packages.size) {
        // Auto-refresh trigger
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
                    IconButton(onClick = { (context as? BaseActivity)?.finish() }) {
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
                    items(packages) { pkg ->
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
                            },
                            onToggleActive = { isActive ->
                                // 🔧 FIX: Toggle active status without deleting
                                val updatedPackage = pkg.copy(isActive = isActive)
                                viewModel.updatePackage(pkg.id, updatedPackage) { success, _ ->
                                    if (success) {
                                        // Refresh the list
                                        viewModel.getPackagesByDoctor(doctorId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && packageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Package?") },
                text = { Text("Are you sure you want to delete '${packageToDelete?.packageName}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            packageToDelete?.let { pkg ->
                                viewModel.deletePackage(pkg.id) { success, message ->
                                    if (success) {
                                        viewModel.getPackagesByDoctor(doctorId)
                                    }
                                }
                            }
                            showDeleteDialog = false
                            packageToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
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
    onViewBookings: () -> Unit,
    onToggleActive: (Boolean) -> Unit // 🔧 FIX: Added toggle callback
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewBookings() },
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, "Edit", tint = SageGreen)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
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

                // 🔧 FIX: Active/Inactive Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (packageModel.isActive) "Active" else "Inactive",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (packageModel.isActive) SageGreen else Color.Red
                    )
                    Switch(
                        checked = packageModel.isActive,
                        onCheckedChange = { newStatus ->
                            onToggleActive(newStatus)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SageGreen,
                            checkedTrackColor = SageGreen.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }
        }
    }
}