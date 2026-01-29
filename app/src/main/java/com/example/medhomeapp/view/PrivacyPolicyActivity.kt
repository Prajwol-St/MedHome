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
                text = stringResource(R.string.title_privacy_policy),
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
                    text = stringResource(R.string.last_updated),
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                PolicySection(
                    title = stringResource(R.string.policy_section_1_title),
                    content = stringResource(R.string.policy_section_1_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_2_title),
                    content = stringResource(R.string.policy_section_2_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_3_title),
                    content = stringResource(R.string.policy_section_3_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_4_title),
                    content = stringResource(R.string.policy_section_4_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_5_title),
                    content = stringResource(R.string.policy_section_5_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_6_title),
                    content = stringResource(R.string.policy_section_6_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_7_title),
                    content = stringResource(R.string.policy_section_7_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_8_title),
                    content = stringResource(R.string.policy_section_8_content)
                )

                PolicySection(
                    title = stringResource(R.string.policy_section_9_title),
                    content = stringResource(R.string.policy_section_9_content)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Divider(
                    color = MintGreen.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = stringResource(R.string.policy_footer),
                    style = TextStyle(
                        color = TextGray,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp
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