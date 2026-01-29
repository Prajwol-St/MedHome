package com.example.medhomeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.medhomeapp.R
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.BloodDonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodRequestDetailsScreen(
    request: BloodRequestModel?,
    viewModel: BloodDonationViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    if (request == null) {
        onBackClick()
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    val currentUserId = viewModel.getCurrentUserId()
    val isOwner = currentUserId == request.userId
    val successMessage by viewModel.successMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage?.contains("deleted") == true) {
            viewModel.clearSuccessMessage()
            onDeleteSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = { Text(stringResource(R.string.request_details), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick){
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit),
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = Color.White
                            )
                        }
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


            if (successMessage != null && !successMessage!!.contains("deleted")) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
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
                                text = successMessage ?: "",
                                color = Color(0xFF388E3C),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearSuccessMessage() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close),
                                    tint = Color(0xFF388E3C)
                                )
                            }
                        }
                    }
                }
            }


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

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = MintGreen,
                                shape = CircleShape,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = request.bloodGroup,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }


                        Surface(
                            color = when (request.urgency) {
                                "Urgent" -> Color(0xFFFFEBEE)
                                "Within 24 hours" -> Color(0xFFFFF8E1)
                                else -> Color(0xFFE8F5E9)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (request.urgency) {
                                        "Urgent" -> Icons.Default.Warning
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when (request.urgency) {
                                        "Urgent" -> Color(0xFFD32F2F)
                                        "Within 24 hours" -> Color(0xFFF57C00)
                                        else -> Color(0xFF388E3C)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = when (request.urgency) {
                                        "Urgent" -> stringResource(R.string.urgent)
                                        "Within 24 hours" -> stringResource(R.string.within_24_hours)
                                        "Within a week" -> stringResource(R.string.within_a_week)
                                        else -> request.urgency
                                    },
                                    color = when (request.urgency) {
                                        "Urgent" -> Color(0xFFD32F2F)
                                        "Within 24 hours" -> Color(0xFFF57C00)
                                        else -> Color(0xFF388E3C)
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider()


                        Text(
                            text = stringResource(R.string.patient_information),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )

                        DetailRow(
                            label = stringResource(R.string.patient_name),
                            value = request.patientName.ifEmpty { stringResource(R.string.anonymous) }
                        )

                        DetailRow(
                            label = stringResource(R.string.units_needed_label),
                            value = stringResource(R.string.units_needed_value, request.unitsNeeded)
                        )

                        Divider()


                        Text(
                            text = stringResource(R.string.hospital_information),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.LocationOn,
                            label = stringResource(R.string.hospital),
                            value = request.hospital
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.Place,
                            label = stringResource(R.string.location),
                            value = request.location
                        )

                        Divider()


                        Text(
                            text = stringResource(R.string.contact_information),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.Phone,
                            label = stringResource(R.string.contact_number),
                            value = request.contactNumber
                        )

                        Divider()


                        Text(
                            text = stringResource(R.string.status_information),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintGreen
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.status),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Surface(
                                color = when (request.status) {
                                    "active" -> Color(0xFFE8F5E9)
                                    "fulfilled" -> Color(0xFFE3F2FD)
                                    "cancelled" -> Color(0xFFFFEBEE)
                                    else -> Color(0xFFF5F5F5)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = when (request.status) {
                                        "active" -> stringResource(R.string.status_active)
                                        "fulfilled" -> stringResource(R.string.status_fulfilled)
                                        "cancelled" -> stringResource(R.string.status_cancelled)
                                        else -> request.status.replaceFirstChar { it.uppercase() }
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (request.status) {
                                        "active" -> Color(0xFF388E3C)
                                        "fulfilled" -> Color(0xFF1976D2)
                                        "cancelled" -> Color(0xFFD32F2F)
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        DetailRowWithIcon(
                            icon = Icons.Default.DateRange,
                            label = stringResource(R.string.posted),
                            value = getFormattedDate(request.timestamp)
                        )

                        DetailRow(
                            label = stringResource(R.string.time_ago_label),
                            value = getTimeAgo(request.timestamp)
                        )


                        if (request.additionalNotes.isNotEmpty()) {
                            Divider()
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.additional_notes_title),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreen
                                )
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = request.additionalNotes,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2D3436),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }


            if (isOwner) {
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
                                text = stringResource(R.string.manage_request),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )

                            if (request.status == "active") {
                                Button(
                                    onClick = { showStatusDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.mark_as_fulfilled))
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.cancelBloodRequest(request.id)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFF57C00)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.cancel_request))
                                }
                            } else if (request.status == "cancelled") {
                                Button(
                                    onClick = {
                                        viewModel.reactivateBloodRequest(request.id)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MintGreen
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.reactivate_request))
                                }
                            } else if (request.status == "fulfilled") {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE3F2FD)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF1976D2),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.request_fulfilled_message),
                                            fontSize = 14.sp,
                                            color = Color(0xFF1976D2)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {

                item {
                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MintGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            stringResource(R.string.call_contact, request.contactNumber),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    stringResource(R.string.delete_blood_request),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_blood_request_confirmation),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBloodRequest(request.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }


    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    stringResource(R.string.mark_fulfilled_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    stringResource(R.string.fulfill_confirmation),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markRequestAsFulfilled(request.id)
                        showStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(stringResource(R.string.yes_mark_fulfilled))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStatusDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DetailRowWithIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MintGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}