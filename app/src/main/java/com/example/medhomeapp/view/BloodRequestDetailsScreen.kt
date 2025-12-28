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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.model.BloodRequestModel
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.SageGreen
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
                    containerColor = SageGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = { Text("Request Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
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
                                    contentDescription = "Close",
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
                                    contentDescription = "Close",
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
                                color = SageGreen,
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
                                    text = request.urgency,
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
                            text = "Patient Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        DetailRow(
                            label = "Patient Name",
                            value = request.patientName.ifEmpty { "Anonymous" }
                        )

                        DetailRow(
                            label = "Units Needed",
                            value = "${request.unitsNeeded} unit(s)"
                        )

                        Divider()


                        Text(
                            text = "Hospital Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.LocationOn,
                            label = "Hospital",
                            value = request.hospital
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.Place,
                            label = "Location",
                            value = request.location
                        )

                        Divider()


                        Text(
                            text = "Contact Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        DetailRowWithIcon(
                            icon = Icons.Default.Phone,
                            label = "Contact Number",
                            value = request.contactNumber
                        )

                        Divider()


                        Text(
                            text = "Status Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status",
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
                                    text = request.status.replaceFirstChar { it.uppercase() },
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
                            label = "Posted",
                            value = getFormattedDate(request.timestamp)
                        )

                        DetailRow(
                            label = "Time Ago",
                            value = getTimeAgo(request.timestamp)
                        )


                        if (request.additionalNotes.isNotEmpty()) {
                            Divider()
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Additional Notes",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
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
                                text = "Manage Request",
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
                                    Text("Mark as Fulfilled")
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
                                    Text("Cancel Request")
                                }
                            } else if (request.status == "cancelled") {
                                Button(
                                    onClick = {
                                        viewModel.reactivateBloodRequest(request.id)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SageGreen
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reactivate Request")
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
                                            text = "This request has been fulfilled. Thank you for your contribution!",
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
                            containerColor = SageGreen
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
                            "Call ${request.contactNumber}",
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
                    "Delete Blood Request",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this blood request? This action cannot be undone.",
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
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
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
                    "Mark as Fulfilled",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Have you received the required blood donation? This will mark the request as fulfilled.",
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
                    Text("Yes, Mark as Fulfilled")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStatusDialog = false }
                ) {
                    Text("Cancel")
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
                tint = SageGreen,
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