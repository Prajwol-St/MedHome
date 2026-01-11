package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
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

class HealthPackagesActivity : BaseActivity() {

    private val viewModel by lazy {
        HealthPackageViewModel(
            HealthPackageRepoImpl(),
            PackageBookingRepoImpl()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthPackagesScreen(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getActivePackages()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesScreen(viewModel: HealthPackageViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val packages by viewModel.activePackages
    val isLoading by viewModel.isLoading

    var selectedCategory by remember { mutableStateOf("All") }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "General Checkup",
        "Diabetes Care",
        "Heart Health",
        "Wellness & Fitness",
        "Women's Health",
        "Senior Care",
        "Preventive Care",
        "Chronic Disease Management"
    )

    val filteredPackages = remember(packages, selectedCategory) {
        if (selectedCategory == "All") {
            packages
        } else {
            packages.filter { it.category == selectedCategory }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getActivePackages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Packages", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? HealthPackagesActivity)?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, MyPackagesActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.ShoppingBag, "My Packages", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundCream)
        ) {
            // Category Filter
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryMenu = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = SageGreen)
                        Column {
                            Text("Category", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                selectedCategory,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark
                            )
                        }
                    }
                    Icon(Icons.Default.ArrowDropDown, null, tint = SageGreen)
                }

                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                showCategoryMenu = false
                            },
                            leadingIcon = {
                                if (selectedCategory == category) {
                                    Icon(Icons.Default.Check, null, tint = SageGreen)
                                }
                            }
                        )
                    }
                }
            }

            // Packages Grid
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SageGreen)
                }
            } else if (filteredPackages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No packages available",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "Check back later for new packages",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPackages, key = { it.id }) { pkg ->
                        PatientPackageCard(
                            packageModel = pkg,
                            onClick = {
                                val intent = Intent(context, HealthPackageDetailsActivity::class.java)
                                intent.putExtra("package_id", pkg.id)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientPackageCard(
    packageModel: HealthPackageModel,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

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
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Package Image
            Box {
                if (packageModel.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(packageModel.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Package Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(LightSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = SageGreen.copy(alpha = 0.5f)
                        )
                    }
                }

                // Days remaining badge
                if (daysRemaining != null && daysRemaining > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            daysRemaining <= 7 -> Color(0xFFFF9800)
                            else -> SageGreen
                        }
                    ) {
                        Text(
                            "$daysRemaining days",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Package Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    packageModel.packageName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    packageModel.category,
                    fontSize = 11.sp,
                    color = SageGreen,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "NPR ${packageModel.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )

                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = SageGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}