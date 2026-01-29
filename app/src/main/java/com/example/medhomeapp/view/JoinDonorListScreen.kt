package com.example.medhomeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.viewmodel.BloodDonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinDonorListScreen(
    viewModel: BloodDonationViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var bloodGroup by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(false) }
    var isEmergencyAvailable by remember { mutableStateOf(false) }

    val donorProfile by viewModel.donorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDonorProfile()
    }

    LaunchedEffect(donorProfile) {
        donorProfile?.let {
            bloodGroup = it.bloodGroup
            isAvailable = it.isAvailable
            isEmergencyAvailable = it.isEmergencyAvailable
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            viewModel.clearSuccessMessage()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                title = { Text(stringResource(R.string.join_donor_list), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.donor_community_info),
                            fontSize = 14.sp,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Error message
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
                                    contentDescription = stringResource(R.string.close),
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }

            // Main Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.donor_information),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )

                        Divider()

                        // Blood Group Selection
                        Text(stringResource(R.string.blood_group_required), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded && !isLoading }
                        ) {
                            OutlinedTextField(
                                value = bloodGroup,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text(stringResource(R.string.select_blood_group)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MintGreen
                                ),
                                enabled = !isLoading
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                                    DropdownMenuItem(
                                        text = { Text(group) },
                                        onClick = {
                                            bloodGroup = group
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Divider()

                        Text(
                            text = stringResource(R.string.availability),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )

                        // Regular Availability
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Checkbox(
                                    checked = isAvailable,
                                    onCheckedChange = { isAvailable = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MintGreen
                                    ),
                                    enabled = !isLoading
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.currently_available),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2D3436)
                                    )
                                    Text(
                                        text = stringResource(R.string.regular_donations_contact),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // Emergency Availability
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEmergencyAvailable) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Checkbox(
                                    checked = isEmergencyAvailable,
                                    onCheckedChange = { isEmergencyAvailable = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MintGreen
                                    ),
                                    enabled = !isLoading
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.emergency_available),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2D3436)
                                    )
                                    Text(
                                        text = stringResource(R.string.urgent_cases_contact),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Donation Guidelines Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.donation_requirements),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3436)
                        )

                        Divider()

                        RequirementItem(requirement = stringResource(R.string.requirement_age))
                        RequirementItem(requirement = stringResource(R.string.requirement_weight))
                        RequirementItem(requirement = stringResource(R.string.requirement_health))
                        RequirementItem(requirement = stringResource(R.string.requirement_wait))
                        RequirementItem(requirement = stringResource(R.string.requirement_illness))
                    }
                }
            }

            // Benefits Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.why_donate_blood),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )

                        Text(
                            text = stringResource(R.string.benefit_save_lives),
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )

                        Text(
                            text = stringResource(R.string.benefit_health_screening),
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )

                        Text(
                            text = stringResource(R.string.benefit_blood_flow),
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )

                        Text(
                            text = stringResource(R.string.benefit_community),
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436)
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        viewModel.createOrUpdateDonorProfile(
                            userName = "", // Get from user profile
                            bloodGroup = bloodGroup,
                            isAvailable = isAvailable,
                            isEmergencyAvailable = isEmergencyAvailable,
                            contactNumber = "", // Get from user profile
                            location = "" // Get from user profile
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && bloodGroup.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (donorProfile != null) stringResource(R.string.update_donor_profile) else stringResource(R.string.save_join_donor_list),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Privacy Notice
            item {
                Text(
                    text = stringResource(R.string.privacy_notice),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RequirementItem(requirement: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = MintGreen,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.size(8.dp)
        ) {}
        Text(
            text = requirement,
            fontSize = 14.sp,
            color = Color(0xFF2D3436)
        )
    }
}