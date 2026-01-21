package com.example.medhomeapp.view

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.repository.NotificationRepositoryImpl
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.utils.DateTimeUtils
import com.example.medhomeapp.utils.NotificationConstants
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.NotificationHistoryViewModel

class NotificationHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotificationHistoryScreen()
        }
    }
}

@Composable
fun NotificationHistoryScreen() {
    val context = LocalContext.current
    val sharedPrefs = (context as BaseActivity).getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    val userId = sharedPrefs.getString("user_id", null)

    val viewModel = remember { NotificationHistoryViewModel(NotificationRepositoryImpl()) }

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadNotifications(it) }
    }

    val notifications by viewModel.notifications
    val unreadCount by viewModel.unreadCount
    val isLoading by viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MintGreen)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { (context as BaseActivity).finish() }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (unreadCount > 0) {
                TextButton(
                    onClick = {
                        userId?.let { viewModel.markAllAsRead(it) }
                    }
                ) {
                    Text(
                        text = "Mark all read",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MintGreen)
            }
        } else if (notifications.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(40.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextGray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You're all caught up!",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Notification List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onRead = {
                            if (!notification.isRead) {
                                userId?.let {
                                    viewModel.markAsRead(it, notification.notificationId)
                                }
                            }
                        },
                        onDelete = {
                            userId?.let {
                                viewModel.deleteNotification(it, notification.notificationId)
                            }
                        }
                    )
                }
            }
        }
    }
}

// New composable without header for inline use
@Composable
fun NotificationHistoryScreenContent() {
    val context = LocalContext.current
    val sharedPrefs = try {
        (context as? BaseActivity)?.getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    } catch (e: Exception) {
        context.getSharedPreferences("MedHomePrefs", MODE_PRIVATE)
    }
    val userId = sharedPrefs?.getString("user_id", null)

    val viewModel = remember { NotificationHistoryViewModel(NotificationRepositoryImpl()) }

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadNotifications(it) }
    }

    val notifications by viewModel.notifications
    val unreadCount by viewModel.unreadCount
    val isLoading by viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        // Header without back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (unreadCount > 0) {
                    Badge(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (unreadCount > 0) {
                TextButton(
                    onClick = {
                        userId?.let { viewModel.markAllAsRead(it) }
                    }
                ) {
                    Text(
                        text = "Mark all read",
                        color = MintGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MintGreen)
            }
        } else if (notifications.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(40.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextGray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You're all caught up!",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Notification List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onRead = {
                            if (!notification.isRead) {
                                userId?.let {
                                    viewModel.markAsRead(it, notification.notificationId)
                                }
                            }
                        },
                        onDelete = {
                            userId?.let {
                                viewModel.deleteNotification(it, notification.notificationId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationHistoryModel,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onRead() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else MintGreen.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DateTimeUtils.formatTimestampForDisplay(notification.timestamp),
                        fontSize = 11.sp,
                        color = TextGray.copy(alpha = 0.7f)
                    )

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Notification", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this notification?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun getNotificationIcon(type: String) = when (type) {
    NotificationConstants.APPOINTMENT_24H,
    NotificationConstants.APPOINTMENT_1H -> Icons.Default.Event
    NotificationConstants.MEDICINE_REMINDER -> Icons.Default.Medication
    NotificationConstants.BOOKING_CONFIRMATION -> Icons.Default.CheckCircle
    NotificationConstants.APPOINTMENT_CANCELLED -> Icons.Default.Cancel
    NotificationConstants.APPOINTMENT_RESCHEDULED -> Icons.Default.Update
    else -> Icons.Default.Notifications
}

@Composable
fun getNotificationColor(type: String) = when (type) {
    NotificationConstants.APPOINTMENT_24H,
    NotificationConstants.APPOINTMENT_1H -> Color(0xFF2196F3)
    NotificationConstants.MEDICINE_REMINDER -> Color(0xFF9C27B0)
    NotificationConstants.BOOKING_CONFIRMATION -> Color(0xFF4CAF50)
    NotificationConstants.APPOINTMENT_CANCELLED -> Color(0xFFD32F2F)
    NotificationConstants.APPOINTMENT_RESCHEDULED -> Color(0xFFFF9800)
    else -> MintGreen
}