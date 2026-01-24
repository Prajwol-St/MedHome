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
import com.example.medhomeapp.model.OrderModel
import com.example.medhomeapp.repository.InventoryRepositoryImpl
import com.example.medhomeapp.repository.OrderRepoImpl
import com.example.medhomeapp.viewmodel.InventoryViewModel
import com.example.medhomeapp.viewmodel.OrderViewModel
import android.widget.Toast

@Composable
fun PharmacyScreen(
    onNavigateToMyOrders: () -> Unit = {} // Add navigation callback
) {
    val context = LocalContext.current
    val inventoryRepo = remember { InventoryRepositoryImpl() }
    val inventoryViewModel = remember { InventoryViewModel(inventoryRepo) }

    val orderRepo = remember { OrderRepoImpl() }
    val orderViewModel = remember { OrderViewModel(orderRepo) }

    // Dialog state
    var showBuyDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<InventoryModel?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    val inventoryList by inventoryViewModel.allInventory.observeAsState(emptyList())
    val isLoading by inventoryViewModel.loading.observeAsState(false)

    // Order state
    val orderStatus by orderViewModel.orderStatus.observeAsState()
    val isOrderLoading by orderViewModel.loading.observeAsState(false)

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

    // Handle order status changes
    LaunchedEffect(orderStatus) {
        orderStatus?.let { (success, message) ->
            if (success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                showBuyDialog = false
                selectedMedicine = null
                orderViewModel.clearStatus()
                // Navigate to My Orders screen after successful order
                onNavigateToMyOrders()
            } else if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                orderViewModel.clearStatus()
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
                                    showBuyDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Buy Dialog with Order Form
        if (showBuyDialog && selectedMedicine != null) {
            BuyMedicineDialog(
                medicine = selectedMedicine!!,
                isLoading = isOrderLoading,
                onDismiss = {
                    showBuyDialog = false
                    selectedMedicine = null
                },
                onPlaceOrder = { order ->
                    orderViewModel.createOrder(order)
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
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = "Medicine",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
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
                        text = "Stock: ${inventory.amount}",
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
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onPlaceOrder: (OrderModel) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var deliveryAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var addressError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    val totalAmount = (medicine.price.toDoubleOrNull() ?: 0.0) * quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Place Order",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    // Medicine Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (medicine.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = medicine.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color.White, RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column {
                                Text(
                                    text = medicine.medicineName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${medicine.price}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF4A6741),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    // Quantity Selector
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF4A6741), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = {
                                val availableStock = medicine.amount.toIntOrNull() ?: 0
                                if (quantity < availableStock) quantity++
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF4A6741), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White
                            )
                        }
                    }
                }

                item {
                    // Delivery Address
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = {
                            deliveryAddress = it
                            addressError = false
                        },
                        label = { Text("Delivery Address *") },
                        placeholder = { Text("Enter your full address") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = addressError,
                        supportingText = {
                            if (addressError) {
                                Text("Address is required", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                phoneNumber = it
                                phoneError = false
                            }
                        },
                        label = { Text("Phone Number *") },
                        placeholder = { Text("10-digit mobile number") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = phoneError,
                        supportingText = {
                            if (phoneError) {
                                Text("Valid 10-digit phone number required", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    // Total Amount
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4A6741).copy(alpha = 0.1f)
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
                                text = "Total Amount:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "₹${"%.2f".format(totalAmount)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A6741)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    var hasError = false

                    if (deliveryAddress.isBlank()) {
                        addressError = true
                        hasError = true
                    }

                    if (phoneNumber.length != 10) {
                        phoneError = true
                        hasError = true
                    }

                    if (!hasError) {
                        // Create order
                        val order = OrderModel(
                            inventoryID = medicine.inventoryID,
                            medicineName = medicine.medicineName,
                            price = medicine.price,
                            quantity = quantity,
                            totalAmount = totalAmount,
                            imageUrl = medicine.imageUrl,
                            orderStatus = "Pending",
                            deliveryAddress = deliveryAddress,
                            phoneNumber = phoneNumber
                        )
                        onPlaceOrder(order)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Placing Order..." else "PLACE ORDER",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}
