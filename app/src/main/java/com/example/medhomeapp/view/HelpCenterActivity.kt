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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.view.ui.theme.MintGreen

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
                .background(MintGreen)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
            Text(
                text = stringResource(R.string.help_center_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.contact_support),
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        ContactCard(
            icon = Icons.Default.Email,
            title = stringResource(R.string.email_support),
            subtitle = "medhome0001@gmail.com",
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:medhome0001@gmail.com")
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        context.getString(R.string.support_email_subject)
                    )
                }
                context.startActivity(intent)
            }
        )

        ContactCard(
            icon = Icons.Default.BugReport,
            title = stringResource(R.string.report_bug),
            subtitle = stringResource(R.string.help_improve_app),
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:medhome0001@gmail.com")
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        context.getString(R.string.bug_report_subject)
                    )
                }
                context.startActivity(intent)
            }
        )

        ContactCard(
            icon = Icons.Default.Feedback,
            title = stringResource(R.string.send_feedback),
            subtitle = stringResource(R.string.share_suggestions),
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO
                ).apply {
                    data = Uri.parse("mailto:medhome0001@gmail.com")
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        context.getString(R.string.support_email_subject)
                    )
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.faq_title),
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        val faqs = listOf(
            stringResource(R.string.faq_q1) to stringResource(R.string.faq_a1),
            stringResource(R.string.faq_q2) to stringResource(R.string.faq_a2),
            stringResource(R.string.faq_q3) to stringResource(R.string.faq_a3),
            stringResource(R.string.faq_q4) to stringResource(R.string.faq_a4),
            stringResource(R.string.faq_q5) to stringResource(R.string.faq_a5),
            stringResource(R.string.faq_q6) to stringResource(R.string.faq_a6),
            stringResource(R.string.faq_q7) to stringResource(R.string.faq_a7),
            stringResource(R.string.faq_q8) to stringResource(R.string.faq_a8),
            stringResource(R.string.faq_q9) to stringResource(R.string.faq_a9),
            stringResource(R.string.faq_q10) to stringResource(R.string.faq_a10)
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
                    .background(MintGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = MintGreen,
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
                    contentDescription = stringResource(
                        if (isExpanded) R.string.collapse else R.string.expand
                    ),
                    tint = MintGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MintGreen.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = answer,
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}