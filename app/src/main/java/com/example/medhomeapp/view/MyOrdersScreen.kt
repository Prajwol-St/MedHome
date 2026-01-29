package com.example.medhomeapp.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.medhomeapp.R
import com.example.medhomeapp.model.OrderModel
import com.example.medhomeapp.repository.OrderRepoImpl
import com.example.medhomeapp.viewmodel.OrderViewModel
import android.widget.Toast
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

    val filterOptions = listOf("All", "Pending", "Processing", "Completed", "Cancelled")

    val filteredOrders = remember(ordersList, selectedFilter) {
        if (selectedFilter == "All") {
            ordersList.sortedByDescending { it.timestamp }
        } else {
            ordersList.filter { it.orderStatus.equals(selectedFilter, ignoreCase = true) }
                .sortedByDescending { it.timestamp }
        }
    }

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

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            orderViewModel.listenToUserOrders(currentUserId)
        } else {
            Toast.makeText(context, context.getString(R.string.please_login_to_view_orders), Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            orderViewModel.stopListening()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filter Chips
            AnimatedVisibility(
                visible = ordersList.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filterOptions.size) { index ->
                        val filter = filterOptions[index]
                        val count = if (filter == "All") {
                            ordersList.size
                        } else {
                            ordersList.count { it.orderStatus.equals(filter, ignoreCase = true) }
                        }

                        FilterChipEnhanced(
                            label = when(filter) {
                                "All" -> stringResource(R.string.all)
                                "Pending" -> stringResource(R.string.pending)
                                "Processing" -> stringResource(R.string.processing)
                                "Completed" -> stringResource(R.string.completed)
                                "Cancelled" -> stringResource(R.string.cancelled)
                                else -> filter
                            },
                            count = count,
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
            }

            // Orders List
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        LoadingState()
                    }
                    filteredOrders.isEmpty() -> {
                        EmptyState(selectedFilter = selectedFilter)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = filteredOrders,
                                key = { it.orderID }
                            ) { order ->
                                OrderCardEnhanced(
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
        }

        // Order Details Dialog
        if (showOrderDetailsDialog && selectedOrder != null) {
            OrderDetailsDialogEnhanced(
                order = selectedOrder!!,
                onDismiss = {
                    showOrderDetailsDialog = false
                    selectedOrder = null
                }
            )
        }

        // Cancel Order Confirmation Dialog
        if (showCancelDialog && orderToCancel != null) {
            CancelOrderDialogEnhanced(
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

@Composable
fun FilterChipEnhanced(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF4A6741) else Color.White,
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF4A6741),
        animationSpec = tween(300)
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = if (selected) 4.dp else 0.dp,
        border = if (!selected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF4A6741).copy(alpha = 0.3f)
        ) else null,
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                fontSize = 14.sp
            )
            if (count > 0) {
                Surface(
                    shape = CircleShape,
                    color = if (selected)
                        Color.White.copy(alpha = 0.3f)
                    else
                        Color(0xFF4A6741).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4A6741),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.loading_your_orders),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EmptyState(selectedFilter: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF4A6741).copy(alpha = 0.1f),
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF4A6741).copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                text = if (selectedFilter == "All") stringResource(R.string.no_orders_yet) else stringResource(R.string.no_filter_orders, selectedFilter),
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2D3436),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.orders_appear_here),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun OrderCardEnhanced(
    order: OrderModel,
    onViewDetails: () -> Unit,
    onCancelOrder: () -> Unit
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                isPressed = true
                onViewDetails()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4A6741).copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color(0xFF4A6741),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "#${order.orderID.take(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                }
                OrderStatusBadgeEnhanced(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Medicine Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Medicine Image
                if (order.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = order.medicineName,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray.copy(alpha = 0.05f)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4A6741).copy(alpha = 0.1f),
                                        Color(0xFF4A6741).copy(alpha = 0.05f)
                                    )
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Medication,
                            contentDescription = null,
                            tint = Color(0xFF4A6741),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Medicine Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = order.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF2D3436)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = stringResource(R.string.qty, order.quantity),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4A6741).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "₹${"%.2f".format(order.totalAmount)}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A6741)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Info & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF4A6741),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF636E72),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = order.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                // Cancel button (only for Pending/Processing orders)
                if (order.orderStatus.equals("Pending", ignoreCase = true) ||
                    order.orderStatus.equals("Processing", ignoreCase = true)
                ) {
                    OutlinedButton(
                        onClick = onCancelOrder,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.cancel), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadgeEnhanced(status: String) {
    val (backgroundColor, textColor, icon) = when (status.lowercase()) {
        "pending" -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF856404),
            Icons.Default.HourglassEmpty
        )
        "processing" -> Triple(
            Color(0xFFD1ECF1),
            Color(0xFF0C5460),
            Icons.Default.LocalShipping
        )
        "completed" -> Triple(
            Color(0xFFD4EDDA),
            Color(0xFF155724),
            Icons.Default.CheckCircle
        )
        "cancelled" -> Triple(
            Color(0xFFF8D7DA),
            Color(0xFF721C24),
            Icons.Default.Cancel
        )
        else -> Triple(
            Color.Gray.copy(alpha = 0.2f),
            Color.DarkGray,
            Icons.Default.FiberManualRecord
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun OrderDetailsDialogEnhanced(
    order: OrderModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4A6741).copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color(0xFF4A6741),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.order_details),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "#${order.orderID.take(12).uppercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.status), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        OrderStatusBadgeEnhanced(status = order.orderStatus)
                    }
                }

                item {
                    if (order.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = order.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Gray.copy(alpha = 0.1f)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8F9FA)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.product_details),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            DetailRowEnhanced(stringResource(R.string.medicine), order.medicineName)
                            DetailRowEnhanced(stringResource(R.string.price_per_unit), "₹${order.price}")
                            DetailRowEnhanced(stringResource(R.string.quantity), "${order.quantity}")
                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                            DetailRowEnhanced(
                                stringResource(R.string.total_amount),
                                "₹${"%.2f".format(order.totalAmount)}",
                                highlight = true
                            )
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8F9FA)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.delivery_information),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            DetailRowEnhanced(stringResource(R.string.address), order.deliveryAddress)
                            DetailRowEnhanced(stringResource(R.string.phone), order.phoneNumber)
                            DetailRowEnhanced(stringResource(R.string.order_time), order.getFormattedTime())
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.close), fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailRowEnhanced(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) Color.Black else Color.Gray,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) Color(0xFF4A6741) else Color(0xFF2D3436),
            modifier = Modifier.weight(1f, fill = false),
            maxLines = if (highlight) 1 else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CancelOrderDialogEnhanced(
    order: OrderModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.cancel_order_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.cancel_order_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF636E72)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8F9FA)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                tint = Color(0xFF4A6741),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = order.medicineName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3436)
                            )
                        }
                        Text(
                            text = stringResource(R.string.order_id_short, order.orderID.take(8).uppercase()),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = stringResource(R.string.amount, "%.2f".format(order.totalAmount)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4A6741)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.yes_cancel_order), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Color(0xFF4A6741)
                )
            ) {
                Text(
                    stringResource(R.string.no_keep_it),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A6741)
                )
            }
        }
    )
}