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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medhomeapp.R
import com.example.medhomeapp.view.ui.theme.MintGreen

@Composable
fun DoctorHomeScreen(
    doctorName: String,
    profilePictureUrl: String?
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val backgroundTint = Color(0xFFF1FBF9)
    val textMain = Color(0xFF2C3E50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundTint)
            .verticalScroll(scrollState)
            .testTag("doctorHomeScreen")
    ) {

        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("doctorWelcomeCard"),
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
                        .background(Color.White.copy(alpha = 0.25f))
                        .testTag("doctorProfilePictureBox"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profilePictureUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.profile_picture),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .testTag("doctorProfileImage"),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile),
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("doctorProfileIcon"),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(R.string.doctor_welcome),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.testTag("doctorWelcomeText")
                    )
                    Text(
                        text = doctorName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.testTag("doctorNameText")
                    )
                }
            }
        }

        // Section Title
        Text(
            text = stringResource(R.string.doctor_management),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textMain,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("doctorManagementTitle")
        )

        // Grid Menu
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .height(300.dp)
                .testTag("doctorServicesGrid")
        ) {

            item {
                DoctorFeatureCard(
                    title = stringResource(R.string.doctor_set_availability),
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFF4DB6AC),
                    tag = "setAvailabilityCard"
                ) {
                    context.startActivity(
                        Intent(context, DoctorAvailabilityActivity::class.java)
                    )
                }
            }

            item {
                DoctorFeatureCard(
                    title = stringResource(R.string.my_appointments),
                    icon = Icons.Default.EventNote,
                    color = Color(0xFF81D4FA),
                    tag = "myAppointmentsCard"
                ) {
                    context.startActivity(
                        Intent(context, DoctorAppointmentsActivity::class.java)
                    )
                }
            }

            item {
                DoctorFeatureCard(
                    title = stringResource(R.string.manage_leaves),
                    icon = Icons.Default.BeachAccess,
                    color = Color(0xFFFF8A80),
                    tag = "manageLeavesCard"
                ) {
                    context.startActivity(
                        Intent(context, ManageLeavesActivity::class.java)
                    )
                }
            }

            item {
                DoctorFeatureCard(
                    title = stringResource(R.string.doctor_health_packages),
                    icon = Icons.Default.LocalShipping,
                    color = Color(0xFF64B5F6),
                    tag = "healthPackagesCard"
                ) {
                    context.startActivity(
                        Intent(context, HealthPackagesManagementActivity::class.java)
                    )
                }
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
    tag: String,
    onClick: () -> Unit
) {
    val textMain = Color(0xFF2C3E50)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick)
            .testTag(tag),
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
                color = textMain,
                lineHeight = 16.sp
            )
        }
    }
}
