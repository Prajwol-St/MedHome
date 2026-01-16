package com.example.medhomeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medhomeapp.model.OrderModel
import com.example.medhomeapp.repository.OrderRepoImpl
import com.example.medhomeapp.viewmodel.OrderViewModel
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyRow
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen() {
    val context = LocalContext.current
    val orderRepo = remember { OrderRepoImpl() }
    val orderViewModel = remember { OrderViewModel(orderRepo) }

    val ordersList by orderViewModel.allOrders.observeAsState(emptyList())
    val isLoading by orderViewModel.loading.observeAsState(false)
    val orderStatus by orderViewModel.orderStatus.observeAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showOrderDetailsDialog by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<OrderModel?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var orderToCancel by remember { mutableStateOf<OrderModel?>(null) }

    // Filter options
    val filterOptions = listOf("All", "Pending", "Completed")

    // Filtered orders based on selected status
    val filteredOrders = remember(ordersList, selectedFilter) {
        if (selectedFilter == "All") {
            ordersList
        } else {
            ordersList.filter { it.orderStatus.equals(selectedFilter, ignoreCase = true) }
        }
    }

    // Handle order status changes (for cancellation)
    LaunchedEffect(orderStatus) {
        orderStatus?.let { (success, message) ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                orderViewModel.clearStatus()
            }
            if (success) {
                showCancelDialog = false
                orderToCancel = null
            }
        }
    }

    // UPDATED: Set up real-time listener for instant updates
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            // Use real-time listener instead of one-time fetch
            orderViewModel.listenToUserOrders(currentUserId)
        } else {
            Toast.makeText(context, "Please login to view orders", Toast.LENGTH_SHORT).show()
        }
    }

    // ADDED: Clean up listener when screen is removed
    DisposableEffect(Unit) {
        onDispose {
            orderViewModel.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Orders",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A6741),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4A6741))
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter Chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterOptions.size) { index ->
                            val filter = filterOptions[index]
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter,
                                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4A6741),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White
                                )
                            )
                        }
                    }

                    // Order count
                    if (filteredOrders.isNotEmpty()) {
                        Text(
                            text = "${filteredOrders.size} order${if (filteredOrders.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    // Orders List
                    if (filteredOrders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = Color.Gray.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = if (selectedFilter == "All") "No orders yet" else "No $selectedFilter orders",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Your orders will appear here instantly",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders, key = { it.orderID }) { order ->
                                OrderCard(
                                    order = order,
                                    onViewDetails = {
                                        selectedOrder = order
                                        showOrderDetailsDialog = true
                                    },
                                    onCancelOrder = {
                                        orderToCancel = order
                                        showCancelDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Order Details Dialog
            if (showOrderDetailsDialog && selectedOrder != null) {
                OrderDetailsDialog(
                    order = selectedOrder!!,
                    onDismiss = {
                        showOrderDetailsDialog = false
                        selectedOrder = null
                    }
                )
            }

            // Cancel Order Confirmation Dialog
            if (showCancelDialog && orderToCancel != null) {
                CancelOrderDialog(
                    order = orderToCancel!!,
                    onConfirm = {
                        orderViewModel.cancelOrder(orderToCancel!!.orderID)
                    },
                    onDismiss = {
                        showCancelDialog = false
                        orderToCancel = null
                    }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderModel,
    onViewDetails: () -> Unit,
    onCancelOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderID.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Medicine Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Medicine Image
                if (order.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = order.medicineName,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.1f)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Medication,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                // Medicine Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = order.medicineName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Quantity: ${order.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${"%.2f".format(order.totalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A6741)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // Cancel button (only for Pending/Processing orders)
                if (order.orderStatus.equals("Pending", ignoreCase = true) ||
                    order.orderStatus.equals("Processing", ignoreCase = true)
                ) {
                    TextButton(
                        onClick = onCancelOrder,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (backgroundColor, textColor, icon, text) = when (status.lowercase()) {
        "pending" -> Tuple4(
            Color(0xFFFFF3CD),
            Color(0xFF856404),
            "⏳",
            "Pending"
        )
        "processing" -> Tuple4(
            Color(0xFFCCE5FF),
            Color(0xFF004085),
            "📦",
            "Processing"
        )
        "completed" -> Tuple4(
            Color(0xFFD4EDDA),
            Color(0xFF155724),
            "✅",
            "Completed"
        )
        "cancelled" -> Tuple4(
            Color(0xFFF8D7DA),
            Color(0xFF721C24),
            "❌",
            "Cancelled"
        )
        else -> Tuple4(
            Color.Gray.copy(alpha = 0.2f),
            Color.DarkGray,
            "•",
            status
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun OrderDetailsDialog(
    order: OrderModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Order Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "#${order.orderID.take(12)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    // Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status:", fontWeight = FontWeight.SemiBold)
                        OrderStatusBadge(status = order.orderStatus)
                    }
                }

                item {
                    // Medicine Image
                    if (order.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = order.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Gray.copy(alpha = 0.1f)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    // Medicine Details
                    DetailRow("Medicine", order.medicineName)
                }

                item {
                    DetailRow("Price per unit", "₹${order.price}")
                }

                item {
                    DetailRow("Quantity", "${order.quantity}")
                }

                item {
                    DetailRow("Total Amount", "₹${"%.2f".format(order.totalAmount)}", highlight = true)
                }

                item {
                    Divider()
                }

                item {
                    // Delivery Info
                    Text("Delivery Information", fontWeight = FontWeight.Bold)
                }

                item {
                    DetailRow("Address", order.deliveryAddress)
                }

                item {
                    DetailRow("Phone", order.phoneNumber)
                }

                item {
                    DetailRow("Order Time", order.getFormattedTime())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741))
            ) {
                Text("CLOSE")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) Color.Black else Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) Color(0xFF4A6741) else Color.Black
        )
    }
}

@Composable
fun CancelOrderDialog(
    order: OrderModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Cancel Order?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Are you sure you want to cancel this order?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = order.medicineName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Order #${order.orderID.take(8)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("YES, CANCEL")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("NO, KEEP IT")
            }
        }
    )
}
