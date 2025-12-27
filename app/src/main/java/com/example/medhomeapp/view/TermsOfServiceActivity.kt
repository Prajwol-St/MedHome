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

class TermsOfServiceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TermsOfServiceScreen()
        }
    }
}

@Composable
fun TermsOfServiceScreen() {
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
                text = "Terms of Service",
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

                TermsSection(
                    title = "1. Acceptance of Terms",
                    content = "By accessing and using MedHome, you accept and agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use our service."
                )

                TermsSection(
                    title = "2. Service Description",
                    content = "MedHome is a healthcare management platform that provides:\n\n" +
                            "• Digital health records management\n" +
                            "• Doctor consultation booking\n" +
                            "• Medication reminders\n" +
                            "• Health information access\n" +
                            "• Emergency contact features\n\n" +
                            "We reserve the right to modify or discontinue services at any time."
                )

                TermsSection(
                    title = "3. User Accounts",
                    content = "You are responsible for:\n\n" +
                            "• Maintaining the confidentiality of your account\n" +
                            "• All activities under your account\n" +
                            "• Providing accurate information\n" +
                            "• Updating your information when necessary\n" +
                            "• Notifying us of unauthorized access\n\n" +
                            "You must be at least 18 years old to create an account."
                )

                TermsSection(
                    title = "4. Acceptable Use",
                    content = "You agree NOT to:\n\n" +
                            "• Use the service for illegal purposes\n" +
                            "• Share false or misleading health information\n" +
                            "• Interfere with the service's operation\n" +
                            "• Attempt to access unauthorized areas\n" +
                            "• Share your account credentials\n" +
                            "• Upload malicious software or code\n" +
                            "• Harass or abuse other users or staff"
                )

                TermsSection(
                    title = "5. Medical Disclaimer",
                    content = "IMPORTANT: MedHome is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of qualified healthcare providers with questions regarding medical conditions. Do not disregard professional medical advice based on information from this app."
                )

                TermsSection(
                    title = "6. Health Information",
                    content = "You acknowledge that:\n\n" +
                            "• You have the right to upload your health data\n" +
                            "• Information provided should be accurate\n" +
                            "• We store data securely but cannot guarantee 100% security\n" +
                            "• You can request data deletion at any time\n" +
                            "• Emergency services may access your data when necessary"
                )

                TermsSection(
                    title = "7. Consultations and Appointments",
                    content = "• Consultations are subject to doctor availability\n" +
                            "• You must provide accurate health information\n" +
                            "• Cancellation policies may apply\n" +
                            "• Payment terms are specified at booking\n" +
                            "• We are not responsible for third-party doctor services"
                )

                TermsSection(
                    title = "8. Intellectual Property",
                    content = "All content, features, and functionality of MedHome are owned by us and protected by international copyright, trademark, and other intellectual property laws. You may not reproduce, distribute, or create derivative works without permission."
                )

                TermsSection(
                    title = "9. Limitation of Liability",
                    content = "MedHome is provided \"as is\" without warranties of any kind. We are not liable for:\n\n" +
                            "• Service interruptions or errors\n" +
                            "• Data loss or security breaches\n" +
                            "• Actions of third-party healthcare providers\n" +
                            "• Decisions made based on app information\n" +
                            "• Technical issues or device compatibility"
                )

                TermsSection(
                    title = "10. Termination",
                    content = "We may suspend or terminate your account if:\n\n" +
                            "• You violate these terms\n" +
                            "• Your account shows suspicious activity\n" +
                            "• Required by law\n" +
                            "• Service is discontinued\n\n" +
                            "You may delete your account at any time through Settings."
                )

                TermsSection(
                    title = "11. Changes to Terms",
                    content = "We reserve the right to modify these terms at any time. Continued use of the service after changes constitutes acceptance of new terms. We will notify you of significant changes."
                )

                TermsSection(
                    title = "12. Governing Law",
                    content = "These Terms are governed by the laws of Nepal. Any disputes will be resolved in the courts of Kathmandu, Nepal."
                )

                TermsSection(
                    title = "13. Contact Information",
                    content = "For questions about these Terms:\n\n" +
                            "Email: legal@medhome.com\n" +
                            "Phone: +977 9800000000\n" +
                            "Address: Kathmandu, Nepal"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "By using MedHome, you acknowledge that you have read, understood, and agree to be bound by these Terms of Service.",
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
fun TermsSection(
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