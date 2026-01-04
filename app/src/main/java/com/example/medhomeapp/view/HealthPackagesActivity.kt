package com.example.medhomeapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthPackage
import com.example.medhomeapp.repository.HealthPackageRepoImpl
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.viewmodel.HealthPackageViewModel

class HealthPackagesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = HealthPackageRepoImpl()
            val viewModel: HealthPackageViewModel = viewModel(
                factory = HealthPackageViewModelFactory(repository)
            )
            HealthPackagesListScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackagesListScreen(viewModel: HealthPackageViewModel) {
    val context = LocalContext.current
    val packages by viewModel.packages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getActivePackages()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Health Packages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SageGreen.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SageGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Choose from our comprehensive health screening packages",
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SageGreen)
                    }
                }
            }

            if (error != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = error ?: "An error occurred",
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            if (packages.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No packages available",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            items(packages.size) { index ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HealthPackageCard(
                        healthPackage = packages[index],
                        onClick = {
                            val intent = Intent(context, HealthPackageDetailsActivity::class.java).apply {
                                putExtra("package_id", packages[index].id)
                                putExtra("package_name", packages[index].name)
                                putExtra("package_description", packages[index].description)
                                putExtra("package_price", packages[index].price)
                                putExtra("package_discount", packages[index].discountPercentage)
                                putExtra("package_duration", packages[index].duration)
                                putExtra("package_recommended", packages[index].recommendedFor)
                                putStringArrayListExtra("package_tests", ArrayList(packages[index].testsIncluded))
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                if (index < packages.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HealthPackageCard(
    healthPackage: HealthPackage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = healthPackage.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = healthPackage.recommendedFor,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                if (healthPackage.hasDiscount()) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${healthPackage.discountPercentage}% OFF",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = healthPackage.description,
                fontSize = 14.sp,
                color = Color(0xFF636E72),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${healthPackage.testsIncluded.size} tests included",
                    fontSize = 13.sp,
                    color = Color(0xFF2D3436),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = healthPackage.duration,
                    fontSize = 13.sp,
                    color = Color(0xFF2D3436)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (healthPackage.hasDiscount()) {
                        Text(
                            text = healthPackage.getFormattedPrice(),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = healthPackage.getFormattedDiscountedPrice(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    } else {
                        Text(
                            text = healthPackage.getFormattedPrice(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

class HealthPackageViewModelFactory(
    private val repository: com.example.medhomeapp.repository.HealthPackageRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthPackageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthPackageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}