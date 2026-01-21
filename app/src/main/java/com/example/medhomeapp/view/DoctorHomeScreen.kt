package com.example.medhomeapp.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.view.ui.theme.MintGreen

@Composable
fun DoctorHomeScreen(doctorName: String, profilePictureUrl: String?) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .verticalScroll(scrollState)
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = MintGreen),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profilePictureUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Welcome Dr.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = doctorName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Section Title
        Text(
            text = "Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Grid Menu - UPDATED with Notifications
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.height(850.dp)
        ) {
            // 1. Notifications - NEW
            item {
                DoctorFeatureCard(
                    title = "Notifications",
                    icon = Icons.Default.Notifications,
                    color = Color(0xFFFF9800),
                    onClick = {
                        val intent = Intent(context, NotificationHistoryActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            // 2. Set Availability
            item {
                DoctorFeatureCard(
                    title = "Set Availability",
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFF6B8E4E),
                    onClick = {
                        context.startActivity(
                            Intent(context, DoctorAvailabilityActivity::class.java)
                        )
                    }
                )
            }

            // 3. My Appointments
            item {
                DoctorFeatureCard(
                    title = "My Appointments",
                    icon = Icons.Default.EventNote,
                    color = Color(0xFF87A96B),
                    onClick = {
                        context.startActivity(
                            Intent(context, DoctorAppointmentsActivity::class.java)
                        )
                    }
                )
            }

            // 4. Manage Leaves
            item {
                DoctorFeatureCard(
                    title = "Manage Leaves",
                    icon = Icons.Default.BeachAccess,
                    color = Color(0xFF6B8E4E),
                    onClick = {
                        context.startActivity(
                            Intent(context, ManageLeavesActivity::class.java)
                        )
                    }
                )
            }

            // 5. Health Packages
            item {
                DoctorFeatureCard(
                    title = "Health Packages",
                    icon = Icons.Default.LocalShipping,
                    color = Color(0xFF87A96B),
                    onClick = {
                        context.startActivity(
                            Intent(context, HealthPackagesManagementActivity::class.java)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DoctorFeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(26.dp),
                    tint = color
                )
            }

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }
    }
}