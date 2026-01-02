package com.example.medhomeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.model.HealthPackage
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.viewmodel.HealthPackageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageFormScreen(
    viewModel: HealthPackageViewModel,
    healthPackage: HealthPackage?,
    isEditMode: Boolean,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf(healthPackage?.name ?: "") }
    var description by remember { mutableStateOf(healthPackage?.description ?: "") }
    var price by remember { mutableStateOf(healthPackage?.price?.toString() ?: "") }
    var discount by remember { mutableStateOf(healthPackage?.discountPercentage?.toString() ?: "0") }
    var duration by remember { mutableStateOf(healthPackage?.duration ?: "") }
    var recommendedFor by remember { mutableStateOf(healthPackage?.recommendedFor ?: "") }
    var testInput by remember { mutableStateOf("") }
    val testsList = remember { mutableStateListOf<String>().apply {
        healthPackage?.testsIncluded?.let { addAll(it) }
    }}

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onSuccess()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        if (isEditMode) "Edit Package" else "Create Package",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error ?: "",
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("Package Name *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Full Body Checkup") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Description *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe what this package includes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Price (NPR) *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = { Text("5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Discount (%)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    placeholder = { Text("10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Duration *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    placeholder = { Text("e.g. 2-3 hours") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Recommended For *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = recommendedFor,
                    onValueChange = { recommendedFor = it },
                    placeholder = { Text("e.g. Adults 18-60") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen
                    ),
                    enabled = !isLoading
                )
            }

            item {
                Text("Tests Included *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = testInput,
                        onValueChange = { testInput = it },
                        placeholder = { Text("Enter test name") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen
                        ),
                        enabled = !isLoading
                    )
                    IconButton(
                        onClick = {
                            if (testInput.isNotBlank()) {
                                testsList.add(testInput.trim())
                                testInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(SageGreen, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Test",
                            tint = Color.White
                        )
                    }
                }
            }

            if (testsList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tests (${testsList.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SageGreen
                            )
                            testsList.forEachIndexed { index, test ->
                                Column {
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
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SageGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = test,
                                                fontSize = 14.sp,
                                                color = Color(0xFF2D3436)
                                            )
                                        }
                                        IconButton(
                                            onClick = { testsList.removeAt(index) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color(0xFFD32F2F),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (index < testsList.size - 1) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (isEditMode && healthPackage != null) {
                            viewModel.updatePackage(
                                packageId = healthPackage.id,
                                name = name,
                                description = description,
                                testsIncluded = testsList.toList(),
                                price = price.toDoubleOrNull() ?: 0.0,
                                discountPercentage = discount.toIntOrNull() ?: 0,
                                duration = duration,
                                recommendedFor = recommendedFor,
                                isActive = healthPackage.isActive
                            )
                        } else {
                            viewModel.createPackage(
                                name = name,
                                description = description,
                                testsIncluded = testsList.toList(),
                                price = price.toDoubleOrNull() ?: 0.0,
                                discountPercentage = discount.toIntOrNull() ?: 0,
                                duration = duration,
                                recommendedFor = recommendedFor
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            if (isEditMode) "Update Package" else "Create Package",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}