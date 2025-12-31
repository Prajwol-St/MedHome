package com.example.medhomeapp.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

// Data class to hold medicine information
data class Medicine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val imageUri: Uri?,
    val name: String,
    val description: String,
    val price: String,
    val amount: String
)

@Composable
fun NotificationScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var medicineList by remember { mutableStateOf<List<Medicine>>(emptyList()) }
    var medicineToEdit by remember { mutableStateOf<Medicine?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Medicine Inventory",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display medicine list
            if (medicineList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No medicines added yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(medicineList, key = { it.id }) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onEdit = {
                                medicineToEdit = medicine
                                showDialog = true
                            },
                            onDelete = {
                                medicineList = medicineList.filter { it.id != medicine.id }
                            }
                        )
                    }
                }
            }
        }

        // FAB with border
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
                .border(
                    width = 2.dp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .alpha(0.8f)
                .clickable {
                    medicineToEdit = null
                    showDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = {
                    medicineToEdit = null
                    showDialog = true
                },
                modifier = Modifier.size(56.dp),
                containerColor = Color.Blue,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Medicine",
                    tint = Color.White
                )
            }
        }

        if (showDialog) {
            AddMedicineDialog(
                existingMedicine = medicineToEdit,
                onDismiss = {
                    showDialog = false
                    medicineToEdit = null
                },
                onSave = { medicine ->
                    if (medicineToEdit != null) {
                        // Edit existing medicine
                        medicineList = medicineList.map {
                            if (it.id == medicine.id) medicine else it
                        }
                    } else {
                        // Add new medicine
                        medicineList = medicineList + medicine
                    }
                    showDialog = false
                    medicineToEdit = null
                }
            )
        }
    }
}

@Composable
fun MedicineCard(
    medicine: Medicine,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image
                if (medicine.imageUri != null) {
                    AsyncImage(
                        model = medicine.imageUri,
                        contentDescription = medicine.name,
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }
                }

                // Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = medicine.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "₹${medicine.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Blue
                        )
                        Text(
                            text = "Qty: ${medicine.amount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.Blue
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun AddMedicineDialog(
    existingMedicine: Medicine?,
    onDismiss: () -> Unit,
    onSave: (Medicine) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(existingMedicine?.imageUri) }
    var medicineName by remember { mutableStateOf(existingMedicine?.name ?: "") }
    var description by remember { mutableStateOf(existingMedicine?.description ?: "") }
    var price by remember { mutableStateOf(existingMedicine?.price ?: "") }
    var amount by remember { mutableStateOf(existingMedicine?.amount ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingMedicine != null) "Edit Medicine" else "Add Medicine",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Gray.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.Gray.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Medicine Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Upload Image",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upload Medicine Image",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        focusedLabelColor = Color.Blue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        focusedLabelColor = Color.Blue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        focusedLabelColor = Color.Blue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount/Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        focusedLabelColor = Color.Blue
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (medicineName.isNotBlank() && price.isNotBlank() && amount.isNotBlank()) {
                            val medicine = Medicine(
                                id = existingMedicine?.id ?: java.util.UUID.randomUUID().toString(),
                                imageUri = selectedImageUri,
                                name = medicineName,
                                description = description,
                                price = price,
                                amount = amount
                            )
                            onSave(medicine)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (existingMedicine != null) "Update Medicine" else "Save Medicine",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}