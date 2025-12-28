package com.example.medhomeapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.ui.theme.BackgroundCream
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.ui.theme.TextDark
import com.example.medhomeapp.ui.theme.TextGray
import com.example.medhomeapp.utils.LanguageManager

class LanguageSelectionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LanguageSelectionScreen()
        }
    }
}

@Composable
fun LanguageSelectionScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("MedHomePrefs", Context.MODE_PRIVATE)

    val fromSettings = (context as ComponentActivity).intent.getBooleanExtra("from_settings", false)

    var selectedLanguage by remember {
        mutableStateOf(LanguageManager.getLanguage(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose language",
            style = TextStyle(
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        LanguageOption(
            text = "English",
            isSelected = selectedLanguage == LanguageManager.ENGLISH,
            onClick = {
                selectedLanguage = LanguageManager.ENGLISH
                LanguageManager.setLanguage(context, LanguageManager.ENGLISH)

                if (fromSettings) {
                    (context as ComponentActivity).recreate()
                } else {
                    sharedPrefs.edit().putBoolean("language_selected", true).apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    context.finish()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LanguageOption(
            text = "नेपाली",
            isSelected = selectedLanguage == LanguageManager.NEPALI,
            onClick = {
                selectedLanguage = LanguageManager.NEPALI
                LanguageManager.setLanguage(context, LanguageManager.NEPALI)

                if (fromSettings) {
                    (context as ComponentActivity).recreate()
                } else {
                    sharedPrefs.edit().putBoolean("language_selected", true).apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    context.finish()
                }
            }
        )
    }
}

@Composable
fun LanguageOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (isSelected) SageGreen.copy(alpha = 0.08f) else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SageGreen else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = if (isSelected) TextDark else TextGray,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 16.sp
            )
        )
    }
}