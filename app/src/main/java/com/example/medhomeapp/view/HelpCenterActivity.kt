package com.example.medhomeapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray

class HelpCenterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HelpCenterScreen()
        }
    }
}

@Composable
fun HelpCenterScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var expandedFaq by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SageGreen)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Help Center",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Contact Support",
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        ContactCard(
            icon = Icons.Default.Email,
            title = "Email Support",
            subtitle = "support@medhome.com",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@medhome.com")
                    putExtra(Intent.EXTRA_SUBJECT, "MedHome Support Request")
                }
                context.startActivity(intent)
            }
        )

        ContactCard(
            icon = Icons.Default.Phone,
            title = "Phone Support",
            subtitle = "+977 9800000000",
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:+9779800000000")
                }
                context.startActivity(intent)
            }
        )

        ContactCard(
            icon = Icons.Default.BugReport,
            title = "Report a Bug",
            subtitle = "Help us improve",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@medhome.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Bug Report - MedHome")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Frequently Asked Questions",
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        val faqs = listOf(
            "How do I book a consultation?" to "Go to the Dashboard and tap 'Book Consultation'. Select your preferred doctor, date, and time slot to complete your booking.",
            "How can I view my health records?" to "Navigate to 'Health Records' from the Dashboard. All your uploaded documents, prescriptions, and reports will be available there.",
            "What is the QR code feature?" to "The QR code allows healthcare providers to quickly access your medical information during emergencies. You can find it on the Dashboard.",
            "How do I set medication reminders?" to "Tap the 'Reminder' tab in the bottom navigation to create and manage your medication reminders.",
            "Can I change my profile information?" to "Yes! Go to Settings > Edit Profile to update your personal information, contact details, and emergency contacts.",
            "How do I reset my password?" to "Go to Settings > Change Password. You'll need to enter your current password and then set a new one.",
            "Is my health data secure?" to "Yes, all your data is encrypted and stored securely. We follow strict healthcare data protection standards. Read our Privacy Policy for more details."
        )

        faqs.forEachIndexed { index, (question, answer) ->
            FaqItem(
                question = question,
                answer = answer,
                isExpanded = expandedFaq == index,
                onClick = {
                    expandedFaq = if (expandedFaq == index) null else index
                }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SageGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = SageGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 13.sp
                    )
                )
            }

            Icon(
                painter = painterResource(R.drawable.baseline_arrow_right_24),
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = TextStyle(
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(
                        if (isExpanded) R.drawable.baseline_expand_less_24
                        else R.drawable.baseline_expand_more_24
                    ),
                    contentDescription = null,
                    tint = SageGreen
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}