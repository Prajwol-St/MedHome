package com.example.medhomeapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class PrivacyPolicyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrivacyPolicyScreen()
        }
    }
}

@Composable
fun PrivacyPolicyScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
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
                text = "Privacy Policy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Last Updated: December 27, 2025",
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                PolicySection(
                    title = "1. Information We Collect",
                    content = "We collect information you provide directly to us, including:\n\n" +
                            "• Personal information (name, email, phone number, date of birth)\n" +
                            "• Health information (medical records, prescriptions, blood group)\n" +
                            "• Emergency contact details\n" +
                            "• Location data for emergency services\n" +
                            "• Usage data and app interactions"
                )

                PolicySection(
                    title = "2. How We Use Your Information",
                    content = "We use the collected information to:\n\n" +
                            "• Provide and maintain our healthcare services\n" +
                            "• Process appointments and consultations\n" +
                            "• Store and manage your health records securely\n" +
                            "• Send medication reminders and notifications\n" +
                            "• Improve our app features and user experience\n" +
                            "• Comply with legal obligations"
                )

                PolicySection(
                    title = "3. Data Security",
                    content = "We take your data security seriously:\n\n" +
                            "• All data is encrypted in transit and at rest\n" +
                            "• We use industry-standard security protocols\n" +
                            "• Regular security audits and updates\n" +
                            "• Secure cloud storage infrastructure\n" +
                            "• Limited access to authorized personnel only"
                )

                PolicySection(
                    title = "4. Data Sharing",
                    content = "We do not sell your personal information. We may share data with:\n\n" +
                            "• Healthcare providers you've authorized\n" +
                            "• Emergency services when necessary\n" +
                            "• Service providers who assist our operations\n" +
                            "• Legal authorities when required by law"
                )

                PolicySection(
                    title = "5. Your Rights",
                    content = "You have the right to:\n\n" +
                            "• Access your personal data\n" +
                            "• Correct inaccurate information\n" +
                            "• Request deletion of your data\n" +
                            "• Opt-out of marketing communications\n" +
                            "• Download your health records\n" +
                            "• Withdraw consent at any time"
                )

                PolicySection(
                    title = "6. Data Retention",
                    content = "We retain your data for as long as your account is active or as needed to provide services. Medical records are retained according to healthcare regulations and legal requirements."
                )

                PolicySection(
                    title = "7. Children's Privacy",
                    content = "Our service is not intended for users under 18 years of age. We do not knowingly collect information from children. If you believe we have collected information from a child, please contact us immediately."
                )

                PolicySection(
                    title = "8. Third-Party Services",
                    content = "We use third-party services for:\n\n" +
                            "• Authentication (Firebase)\n" +
                            "• Cloud storage and hosting\n" +
                            "• Payment processing\n" +
                            "• Analytics and crash reporting\n\n" +
                            "These services have their own privacy policies."
                )

                PolicySection(
                    title = "9. Contact Us",
                    content = "If you have questions about this Privacy Policy, contact us at:\n\n" +
                            "Email: privacy@medhome.com\n" +
                            "Phone: +977 9800000000\n" +
                            "Address: Kathmandu, Nepal"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "By using MedHome, you agree to this Privacy Policy. We may update this policy periodically, and changes will be posted in the app.",
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun PolicySection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = TextStyle(
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        )
    }
}