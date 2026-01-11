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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.view.ui.theme.*

@Composable
fun HomeScreen(userName: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Using MintUltraLight for background and MintDark for text for better harmony
    val BackgroundTint = Color(0xFFF1FBF9)
    val TextMain = Color(0xFF2C3E50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundTint)
            .verticalScroll(scrollState)
    ) {
        // Welcome Card
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile),
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.welcome),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val intent = Intent(context, QrActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }
            }
        }

        // Services Section
        Text(
            text = "Services",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Services Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.height(700.dp)
        ) {
            item {
                FeatureCard(
                    title = stringResource(R.string.health_records),
                    icon = Icons.Default.Description,
                    color = Color(0xFF4DB6AC), // Mint Dark
                    onClick = {
                        val intent = Intent(context, HealthRecords::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.book_consultation),
                    icon = Icons.Default.VideoCall,
                    color = Color(0xFF81D4FA), // Med Blue
                    onClick = {
                        val intent = Intent(context, BookConsultationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.ai_health_assistant),
                    icon = Icons.Default.Chat,
                    color = Color(0xFF9575CD), // Soft Purple
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.past_bookings),
                    icon = Icons.Default.Event,
                    color = Color(0xFFA5D6A7), // Fresh Leaf
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.appointments),
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFF4DB6AC), // Mint Dark
                    onClick = { }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.calories_calculator),
                    icon = Icons.Default.FitnessCenter,
                    color = Color(0xFFFFB74D), // Warning Gold/Orange
                    onClick = {
                        val intent = Intent(context, CaloriesCalculatorActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.blood_donation),
                    icon = Icons.Default.Favorite,
                    color = Color(0xFFFF8A80), // Soft Coral
                    onClick = {
                        val intent = Intent(context, BloodDonationActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                FeatureCard(
                    title = stringResource(R.string.health_packages),
                    icon = Icons.Default.LocalShipping,
                    color = Color(0xFF64B5F6), // Sky Blue
                    onClick = {
                        val intent = Intent(context, HealthPackagesActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val TextMain = Color(0xFF2C3E50)

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
            horizontalAlignment = Alignment.Start,
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
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMain,
                lineHeight = 16.sp
            )
        }
    }
}