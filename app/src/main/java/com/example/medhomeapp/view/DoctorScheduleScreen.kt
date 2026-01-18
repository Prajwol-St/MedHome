package com.example.medhomeapp.view

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.medhomeapp.model.InventoryModel
import com.example.medhomeapp.repository.InventoryRepositoryImpl
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.viewmodel.InventoryViewModel
import java.io.File

@Composable
fun DoctorScheduleScreen() {
    val context = LocalContext.current
    val repo = remember { InventoryRepositoryImpl() }
    val inventoryViewModel = remember { InventoryViewModel(repo) }

    var showDialog by remember { mutableStateOf(false) }
    var inventoryToEdit by remember { mutableStateOf<InventoryModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var inventoryToDelete by remember { mutableStateOf<InventoryModel?>(null) }

    // Observe inventory and loading state
    val inventoryList by inventoryViewModel.allInventory.observeAsState(emptyList())
    val isLoading by inventoryViewModel.loading.observeAsState(false)
    val inventoryStatus by inventoryViewModel.inventoryStatus.observeAsState()

    // Load user's inventory when composable loads
    LaunchedEffect(Unit) {
        inventoryViewModel.getCurrentUserInventory()
    }

    // Handle inventory operation results
    LaunchedEffect(inventoryStatus) {
        inventoryStatus?.let { (success, message) ->
            if (success) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                showDialog = false
                showDeleteDialog = false
                inventoryToEdit = null
                inventoryToDelete = null
            } else if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            inventoryViewModel.clearStatus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SageGreen)
            }
        } else {
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
                if (inventoryList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "No medicines",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No medicines added yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add your first medicine to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(inventoryList, key = { it.inventoryID }) { inventory ->
                            MedicineCarde(
                                inventory = inventory,
                                onEdit = {
                                    inventoryToEdit = inventory
                                    showDialog = true
                                },
                                onDelete = {
                                    inventoryToDelete = inventory
                                    showDeleteDialog = true
                                }
                            )
                        }
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
                    inventoryToEdit = null
                    showDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = {
                    inventoryToEdit = null
                    showDialog = true
                },
                modifier = Modifier.size(56.dp),
                containerColor = SageGreen,
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
            AddMedicineDialogPop(
                existingInventory = inventoryToEdit,
                viewModel = inventoryViewModel,
                onDismiss = {
                    showDialog = false
                    inventoryToEdit = null
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && inventoryToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(text = "Delete Medicine")
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete ${inventoryToDelete!!.medicineName}? This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            inventoryViewModel.deleteInventory(inventoryToDelete!!.inventoryID)
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel", color = SageGreen)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun MedicineCarde(
    inventory: InventoryModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }

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
                if (inventory.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = inventory.imageUrl,
                        contentDescription = inventory.medicineName,
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
                        text = inventory.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (inventory.description.isNotEmpty()) {
                        Text(
                            text = inventory.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "₹${inventory.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SageGreen
                        )
                        Text(
                            text = "Qty: ${inventory.amount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = inventory.getFormattedTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Dropdown menu
            Box {
                IconButton(
                    onClick = { showDropdown = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Gray
                    )
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Edit", color = Color.Black)
                        },
                        onClick = {
                            showDropdown = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = SageGreen
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Delete", color = Color.Red)
                        },
                        onClick = {
                            showDropdown = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddMedicineDialogPop(
    existingInventory: InventoryModel?,
    viewModel: InventoryViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var medicineName by remember { mutableStateOf(existingInventory?.medicineName ?: "") }
    var description by remember { mutableStateOf(existingInventory?.description ?: "") }
    var price by remember { mutableStateOf(existingInventory?.price ?: "") }
    var amount by remember { mutableStateOf(existingInventory?.amount ?: "") }
    var isUploading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }


    fun getRealPathFromURI(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_medicine_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("ImagePicker", "Error getting file path: ${e.message}")
            null
        }
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
                        text = if (existingInventory != null) "Edit Medicine" else "Add Medicine",
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
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Medicine Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        existingInventory?.imageUrl?.isNotEmpty() == true -> {
                            AsyncImage(
                                model = existingInventory.imageUrl,
                                contentDescription = "Current Medicine Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
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
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
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
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
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
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (medicineName.isNotBlank() && price.isNotBlank() && amount.isNotBlank()) {
                            isUploading = true

                            // Get real file path from URI - EXACTLY LIKE MYPOSTACTIVITY
                            val imagePath = selectedImageUri?.let { uri ->
                                getRealPathFromURI(uri)
                            }

                            if (existingInventory != null) {
                                // Update existing inventory
                                viewModel.updateInventoryWithImage(
                                    inventoryId = existingInventory.inventoryID,
                                    currentInventory = existingInventory,
                                    medicineName = medicineName,
                                    description = description,
                                    price = price,
                                    amount = amount,
                                    imageUri = imagePath
                                )
                            } else {
                                // Create new inventory
                                viewModel.createInventoryWithImage(
                                    medicineName = medicineName,
                                    description = description,
                                    price = price,
                                    amount = amount,
                                    imageUri = imagePath
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (existingInventory != null) "Update Medicine" else "Save Medicine",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
