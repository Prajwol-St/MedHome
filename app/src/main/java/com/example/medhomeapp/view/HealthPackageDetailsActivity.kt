package com.example.medhomeapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.model.HealthPackage
import com.example.medhomeapp.ui.theme.SageGreen

class HealthPackageDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get package data from intent
        val packageId = intent.getStringExtra("package_id") ?: ""
        val packageName = intent.getStringExtra("package_name") ?: ""
        val packageDesc = intent.getStringExtra("package_description") ?: ""
        val packagePrice = intent.getDoubleExtra("package_price", 0.0)
        val packageDiscount = intent.getIntExtra("package_discount", 0)
        val packageDuration = intent.getStringExtra("package_duration") ?: ""
        val packageRecommended = intent.getStringExtra("package_recommended") ?: ""
        val packageTests = intent.getStringArrayListExtra("package_tests") ?: arrayListOf()

        val healthPackage = HealthPackage(
            id = packageId,
            name = packageName,
            description = packageDesc,
            price = packagePrice,
            discountPercentage = packageDiscount,
            duration = packageDuration,
            recommendedFor = packageRecommended,
            testsIncluded = packageTests
        )

        setContent {
            HealthPackageDetailsScreen(healthPackage = healthPackage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPackageDetailsScreen(healthPackage: HealthPackage) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Package Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main package info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = healthPackage.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436),
                                modifier = Modifier.weight(1f)
                            )

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

                        Surface(
                            color = SageGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = healthPackage.recommendedFor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = SageGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Divider()

                        Text(
                            text = "Description",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        Text(
                            text = healthPackage.description,
                            fontSize = 14.sp,
                            color = Color(0xFF636E72),
                            lineHeight = 20.sp
                        )

                        Divider()

                        Text(
                            text = "Package Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SageGreen
                            )
                            Column {
                                Text(
                                    text = "Duration",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = healthPackage.duration,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2D3436)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = SageGreen
                            )
                            Column {
                                Text(
                                    text = "Total Tests",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${healthPackage.testsIncluded.size} tests",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2D3436)
                                )
                            }
                        }
                    }
                }
            }

            // Tests included card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tests Included (${healthPackage.testsIncluded.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        healthPackage.testsIncluded.forEach { test ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = test,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2D3436)
                                )
                            }
                        }
                    }
                }
            }

            // Price card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SageGreen),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Package Price",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            if (healthPackage.hasDiscount()) {
                                Text(
                                    text = healthPackage.getFormattedPrice(),
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Text(
                                    text = healthPackage.getFormattedDiscountedPrice(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Save NPR ${String.format("%.2f", healthPackage.getSavingsAmount())}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            } else {
                                Text(
                                    text = healthPackage.getFormattedPrice(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Book button
            item {
                Button(
                    onClick = {
                        // TODO: Implement booking
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Book This Package",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}