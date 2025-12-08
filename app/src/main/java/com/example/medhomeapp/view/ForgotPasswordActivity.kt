package com.example.medhomeapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.view.ui.theme.MedHomeAppTheme

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordBody()
        }
    }
}


@Composable
fun ForgotPasswordBody(){
    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .background(White)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Forgot Password",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color(0xFF648DDB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24   .dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFF648DDB),
            )
        }
    }

}



@Preview
@Composable
fun PreviewForgotPassword(){
    ForgotPasswordBody()
}