package com.example.medhomeapp.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.medhomeapp.utils.generateQrBitmap
import com.google.firebase.auth.FirebaseAuth

class QrActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrScreen()
        }
    }
}

@Composable
fun QrScreen() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "NO_UID"

    // Generate QR from UID
    val qrBitmap: Bitmap = generateQrBitmap(uid, 600)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Your QR Code")

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "UID:\n$uid")
    }
}
