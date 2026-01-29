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
fun HomeScreen(
    userName: String,
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
            .testTag("homeScreen")
    ) {

        // Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("welcomeCard"),
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
                            .background(Color.White.copy(alpha = 0.25f))
                            .testTag("profilePictureBox"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(profilePictureUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.profile),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .testTag("profileImage"),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = stringResource(R.string.profile),
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("profileIcon"),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.welcome),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.testTag("welcomeText")
                        )
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.testTag("userNameText")
                        )
                    }
                }

                IconButton(
                    onClick = {
                        context.startActivity(
                            Intent(context, QrActivity::class.java)
                        )
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("qrButton")
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "QR",
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }
            }
        }

        // Services title
        Text(
            text = stringResource(R.string.services),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textMain,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("servicesTitle")
        )

        // Services Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .height(700.dp)
                .testTag("servicesGrid")
        ) {

            item {
                FeatureCard(
                    stringResource(R.string.health_records),
                    Icons.Default.Description,
                    Color(0xFF4DB6AC),
                    "healthRecordsCard"
                ) {
                    context.startActivity(
                        Intent(context, HealthRecords::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.book_consultation),
                    Icons.Default.VideoCall,
                    Color(0xFF81D4FA),
                    "bookConsultationCard"
                ) {
                    context.startActivity(
                        Intent(context, BookConsultationActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.ai_health_assistant),
                    Icons.Default.Chat,
                    Color(0xFF9575CD),
                    "aiHealthAssistantCard"
                ) {
                    context.startActivity(
                        Intent(context, ChatbotActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.past_bookings),
                    Icons.Default.Event,
                    Color(0xFFA5D6A7),
                    "pastBookingsCard"
                ) {
                    context.startActivity(
                        Intent(context, PastBookingsActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.appointments),
                    Icons.Default.CalendarMonth,
                    Color(0xFF4DB6AC),
                    "appointmentsCard"
                ) {
                    context.startActivity(
                        Intent(context, MyAppointmentsActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.calories_calculator),
                    Icons.Default.FitnessCenter,
                    Color(0xFFFFB74D),
                    "caloriesCalculatorCard"
                ) {
                    context.startActivity(
                        Intent(context, CaloriesCalculatorActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.blood_donation),
                    Icons.Default.Favorite,
                    Color(0xFFFF8A80),
                    "bloodDonationCard"
                ) {
                    context.startActivity(
                        Intent(context, BloodDonationActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    stringResource(R.string.health_packages),
                    Icons.Default.LocalShipping,
                    Color(0xFF64B5F6),
                    "healthPackagesCard"
                ) {
                    context.startActivity(
                        Intent(context, HealthPackagesActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    "Pharmacy",
                    Icons.Default.LocalPharmacy,
                    Color(0xFF00BCD4),
                    "pharmacyCard"
                ) {
                    context.startActivity(
                        Intent(context, PharmacyActivity::class.java)
                    )
                }
            }

            item {
                FeatureCard(
                    "Medicine Reminders",
                    Icons.Default.Medication,
                    Color(0xFF9C27B0),
                    "medicineRemindersCard"
                ) {
                    context.startActivity(
                        Intent(context, MedicineReminderActivity::class.java)
                    )
                }
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