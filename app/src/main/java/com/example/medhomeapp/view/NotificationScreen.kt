package com.example.medhomeapp.view

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medhomeapp.model.InventoryModel
import com.example.medhomeapp.repository.InventoryRepositoryImpl
import com.example.medhomeapp.viewmodel.InventoryViewModel
import android.widget.Toast
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val repo = remember { InventoryRepositoryImpl() }
    val inventoryViewModel = remember { InventoryViewModel(repo) }

    // Dialog state
    var showBuyDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<InventoryModel?>(null) }
    var quantity by remember { mutableStateOf(1) }

    var searchQuery by remember { mutableStateOf("") }
    val inventoryList by inventoryViewModel.allInventory.observeAsState(emptyList())
    val isLoading by inventoryViewModel.loading.observeAsState(false)

    val filteredList = remember(inventoryList, searchQuery) {
        if (searchQuery.isBlank()) {
            inventoryList
        } else {
            inventoryList.filter { medicine ->
                medicine.medicineName.contains(searchQuery, ignoreCase = true) ||
                        medicine.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        inventoryViewModel.getAllInventory()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    placeholder = { Text("Search medicines...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null, tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A6741),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No medicines found for \"$searchQuery\"" else "No medicines available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "${filteredList.size} medicine${if (filteredList.size != 1) "s" else ""} found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredList, key = { it.inventoryID }) { inventory ->
                            MedicineCardReadOnly(
                                inventory = inventory,
                                onBuyClick = {
                                    selectedMedicine = inventory
                                    quantity = 1
                                    showBuyDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Buy Dialog
        if (showBuyDialog && selectedMedicine != null) {
            BuyMedicineDialog(
                medicine = selectedMedicine!!,
                quantity = quantity,
                onQuantityChange = { quantity = it },
                onDismiss = {
                    showBuyDialog = false
                    selectedMedicine = null
                },
                onBuy = {
                    Toast.makeText(context, "Bought ${quantity} x ${selectedMedicine!!.medicineName}", Toast.LENGTH_LONG).show()
                    showBuyDialog = false
                    selectedMedicine = null
                }
            )
        }
    }
}

@Composable
fun MedicineCardReadOnly(
    inventory: InventoryModel,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBuyClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (inventory.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = inventory.imageUrl,
                    contentDescription = inventory.medicineName,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Color.Gray)
                }
            }

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
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "₹${inventory.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A6741)
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
    }
}

@Composable
fun BuyMedicineDialog(
    medicine: InventoryModel,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onBuy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = medicine.medicineName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (medicine.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = medicine.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Medication,
                            contentDescription = "Medicine",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Price & Stock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₹${medicine.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A6741)
                    )
                    Text(
                        text = "Stock: ${medicine.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                if (medicine.description.isNotEmpty()) {
                    Text(
                        text = medicine.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }

                // Quantity Selector
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Text("-", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Text("+", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    }
                }

                // Total
                Text(
                    text = "Total: ₹${(medicine.price.toDoubleOrNull() ?: 0.0) * quantity}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6741),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741))
            ) {
                Text("BUY NOW", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
